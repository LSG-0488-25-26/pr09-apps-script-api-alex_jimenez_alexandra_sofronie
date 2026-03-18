const API_KEY = PropertiesService.getScriptProperties().getProperty("API_KEY");
const BASE_URL = PropertiesService.getScriptProperties().getProperty("BASE_URL");
const SHEET_NAME = "Hoja Registro"; 

// ===================== doGet(e) =====================
function doGet(e) {
  const apiKey = (e.parameter.apiKey || "").trim();
  if (apiKey !== API_KEY) {
    return _jsonResponse({ status: "error", error: "Unauthorized: API key incorrecta." });
  }
  const type = (e.parameter.type || "").trim();
  switch (type) {
    case "facturas":    return _getFacturas();
    case "buscar":
      const nombre = (e.parameter.nombre || "").trim();
      if (!nombre) return _jsonResponse({ status: "error", error: "Falta el parámetro 'nombre'." });
      return _buscarPorNombre(nombre);
    case "estadisticas": return _getEstadisticas();
    default:
      return _jsonResponse({ status: "error", error: "Endpoint GET no reconocido." });
  }
}

// ===================== doPost(e) =====================
function doPost(e) {
  const body = JSON.parse(e.postData.contents);
  const apiKey = (body.apiKey || "").trim();
  if (apiKey !== API_KEY) {
    return _jsonResponse({ status: "error", error: "Unauthorized: API key incorrecta." });
  }
  const type = (body.type || "").trim();
  switch (type) {
    case "crear":     return _crearFactura(body);
    case "eliminar":  return _eliminarFactura(body);
    case "actualizar": return _actualizarFactura(body);
    default:
      return _jsonResponse({ status: "error", error: "Endpoint POST no reconocido." });
  }
}

// ===================== UTILITAT =====================
function _normalizeKey(key) {
  if (key === "cliente")   return "nombre";
  if (key === "dirección") return "direccion";
  return key;
}

function _sanitizeValue(key, val) {
  if (key === "id" || key === "cantidad") return parseInt(val) || 0;
  if (key === "precioUnitario" || key === "total") return parseFloat(val) || 0;
  if (val instanceof Date) return Utilities.formatDate(val, "Europe/Madrid", "dd/MM/yyyy");
  if (val === null || val === undefined) return "";
  //Si por algún motivo llega un string ISO de fecha, lo formateamos
  if (typeof val === "string" && val.match(/^\d{4}-\d{2}-\d{2}T/)) {
    return Utilities.formatDate(new Date(val), "Europe/Madrid", "dd/MM/yyyy");
  }
  return val;
}

function _getSheet() {
  const sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName(SHEET_NAME);
  if (!sheet) throw new Error("Hoja '" + SHEET_NAME + "' no encontrada.");
  return sheet;
}

function _jsonResponse(obj) {
  return ContentService
    .createTextOutput(JSON.stringify(obj))
    .setMimeType(ContentService.MimeType.JSON);
}

// ===================== FUNCIONES GET =====================
function _getFacturas() {
  try {
    const sheet = _getSheet();
    const data = sheet.getDataRange().getValues();
    const headers = data[0];
    const jsonData = [];
    for (let i = 1; i < data.length; i++) {
      let rowData = {};
      for (let j = 0; j < headers.length; j++) {
        const key = _normalizeKey(headers[j]);
        rowData[key] = _sanitizeValue(key, data[i][j]);
      }
      jsonData.push(rowData);
    }
    return _jsonResponse({ status: "ok", type: "facturas", data: jsonData });
  } catch(e) {
    return _jsonResponse({ status: "error", error: e.message });
  }
}

function _buscarPorNombre(nombreBuscar) {
  try {
    const sheet = _getSheet();
    const data = sheet.getDataRange().getValues();
    const headers = data[0].map(_normalizeKey);
    const nombreColIndex = headers.indexOf("nombre");
    if (nombreColIndex === -1) return _jsonResponse({ status: "error", error: "Columna 'nombre' no trobada." });
    const jsonData = [];
    for (let i = 1; i < data.length; i++) {
      const valorNombre = (data[i][nombreColIndex] || "").toString().toLowerCase();
      if (valorNombre.includes(nombreBuscar.toLowerCase())) {
        let rowData = {};
        for (let j = 0; j < headers.length; j++) {
          rowData[headers[j]] = _sanitizeValue(headers[j], data[i][j]);
        }
        jsonData.push(rowData);
      }
    }
    return _jsonResponse({ status: "ok", type: "buscar", data: jsonData });
  } catch(e) {
    return _jsonResponse({ status: "error", error: e.message });
  }
}

function _getEstadisticas() {
  try {
    const sheet = _getSheet();
    const data = sheet.getDataRange().getValues();
    const headers = data[0].map(_normalizeKey);
    const totalColIndex = headers.indexOf("total");
    let totalFacturas = data.length - 1;
    let sumaTotal = 0;
    if (totalColIndex !== -1) {
      for (let i = 1; i < data.length; i++) {
        sumaTotal += parseFloat(data[i][totalColIndex]) || 0;
      }
    }
    return _jsonResponse({
      status: "ok", type: "estadisticas",
      data: { totalFacturas, sumaTotal, mediaFactura: totalFacturas > 0 ? sumaTotal / totalFacturas : 0 }
    });
  } catch(e) {
    return _jsonResponse({ status: "error", error: e.message });
  }
}

// ===================== FUNCIONES POST =====================
function _crearFactura(body) {
  try {
    const sheet = _getSheet();
    const data = sheet.getDataRange().getValues();
    if (data.length === 0) return _jsonResponse({ status: "error", error: "Capçaleres no trobades." });

    const headers = data[0].map(_normalizeKey);
  
    const idColIndex = headers.indexOf("id");
    let newId = 1;
    if (idColIndex !== -1 && data.length > 1) {
      for (let i = 1; i < data.length; i++) {
        const rowId = parseInt(data[i][idColIndex]) || 0;
        if (rowId >= newId) newId = rowId + 1;
      }
    }

    const cantidad = parseInt(body.cantidad) || 0;
    const precioUnitario = parseFloat(body.precioUnitario) || 0;
    const total = cantidad * precioUnitario;
    const fecha = Utilities.formatDate(new Date(), "Europe/Madrid", "dd/MM/yyyy");

    const newRow = data.length + 1;
    const setCol = (headerName, value) => {
      const idx = headers.indexOf(headerName);
      if (idx !== -1) sheet.getRange(newRow, idx + 1).setValue(value);
    };

    setCol("id", newId);
    setCol("nombre", (body.nombre || body.cliente || "").trim());
    setCol("apellidos", (body.apellidos || "").trim());
    setCol("dni", (body.dni || "").trim());
    setCol("direccion", (body.direccion || body.dirección || "").trim());
    setCol("concepto", (body.concepto || "").trim());
    setCol("cantidad", cantidad);
    setCol("precioUnitario", precioUnitario);
    setCol("total", total);
    setCol("fecha", fecha);

    return _jsonResponse({ status: "ok", message: "Factura creada amb ID " + newId });
  } catch(e) {
    return _jsonResponse({ status: "error", error: e.message });
  }
}

function _eliminarFactura(body) {
  try {
    const id = parseInt(body.id);
    if (isNaN(id)) return _jsonResponse({ status: "error", error: "ID no vàlid." });
    const sheet = _getSheet();
    const data = sheet.getDataRange().getValues();
    const headers = data[0].map(_normalizeKey);
    const idColIndex = headers.indexOf("id");
    if (idColIndex === -1) return _jsonResponse({ status: "error", error: "Columna 'id' no trobada." });
    for (let i = 1; i < data.length; i++) {
      if (parseInt(data[i][idColIndex]) === id) {
        sheet.deleteRow(i + 1);
        return _jsonResponse({ status: "ok", message: "Factura amb ID " + id + " eliminada." });
      }
    }
    return _jsonResponse({ status: "error", error: "No s'ha trobat cap factura amb ID " + id });
  } catch(e) {
    return _jsonResponse({ status: "error", error: e.message });
  }
}

function _actualizarFactura(body) {
  try {
    const id = parseInt(body.id);
    if (isNaN(id)) return _jsonResponse({ status: "error", error: "ID no vàlid." });
    const sheet = _getSheet();
    const data = sheet.getDataRange().getValues();
    const headers = data[0].map(_normalizeKey);
    const idColIndex = headers.indexOf("id");
    if (idColIndex === -1) return _jsonResponse({ status: "error", error: "Columna 'id' no trobada." });
    for (let i = 1; i < data.length; i++) {
      if (parseInt(data[i][idColIndex]) === id) {
        const row = i + 1;
        const setCol = (headerName, value) => {
          const idx = headers.indexOf(headerName);
          if (idx !== -1) sheet.getRange(row, idx + 1).setValue(value);
        };
        if (body.nombre !== undefined)         setCol("nombre", body.nombre);
        if (body.apellidos !== undefined)      setCol("apellidos", body.apellidos);
        if (body.dni !== undefined)            setCol("dni", body.dni);
        if (body.direccion !== undefined)      setCol("direccion", body.direccion);
        if (body.concepto !== undefined)       setCol("concepto", body.concepto);
        if (body.cantidad !== undefined)       setCol("cantidad", parseInt(body.cantidad));
        if (body.precioUnitario !== undefined) setCol("precioUnitario", parseFloat(body.precioUnitario));
        const cant  = body.cantidad      ? parseInt(body.cantidad)        : parseInt(data[i][headers.indexOf("cantidad")]) || 0;
        const precio = body.precioUnitario ? parseFloat(body.precioUnitario) : parseFloat(data[i][headers.indexOf("precioUnitario")]) || 0;
        setCol("total", cant * precio);
        return _jsonResponse({ status: "ok", message: "Factura amb ID " + id + " actualitzada." });
      }
    }
    return _jsonResponse({ status: "error", error: "No s'ha trobat cap factura amb ID " + id });
  } catch(e) {
    return _jsonResponse({ status: "error", error: e.message });
  }
}

// ===================== FUNCIONS DE TEST =====================
function testGetFacturas() {
  const mock = { parameter: { apiKey: API_KEY, type: "facturas" } };
  Logger.log(doGet(mock).getContent());
}
function testGetBuscar() {
  const mock = { parameter: { apiKey: API_KEY, type: "buscar", nombre: "Joan" } };
  Logger.log(doGet(mock).getContent());
}
function testGetEstadisticas() {
  const mock = { parameter: { apiKey: API_KEY, type: "estadisticas" } };
  Logger.log(doGet(mock).getContent());
}
function testPostCrear() {
  const mock = {
    postData: {
      contents: JSON.stringify({
        apiKey: API_KEY, type: "crear",
        nombre: "Joan Garcia", apellidos: "Garcia Lopez",
        dni: "12345678A", direccion: "Carrer Major 123",
        concepto: "Disseny web", cantidad: 3, precioUnitario: 150.00
      })
    }
  };
  Logger.log(doPost(mock).getContent());
}
function testPostEliminar() {
  const mock = { postData: { contents: JSON.stringify({ apiKey: API_KEY, type: "eliminar", id: 1 }) } };
  Logger.log(doPost(mock).getContent());
}
function testPostActualizar() {
  const mock = {
    postData: {
      contents: JSON.stringify({
        apiKey: API_KEY, type: "actualizar", id: 2,
        nombre: "Maria López", apellidos: "López Sánchez",
        dni: "87654321B", direccion: "Plaça Central 5", cantidad: 5
      })
    }
  };
  Logger.log(doPost(mock).getContent());
}
