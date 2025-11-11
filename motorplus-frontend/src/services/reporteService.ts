import apiClient from './apiClient';

export interface ReportData {
  [key: string]: any;
}

export const reporteService = {
  // Reportes Simples
  async getClientesReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/1/clientes');
    return response.data;
  },

  async getVehiculosReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/2/vehiculos');
    return response.data;
  },

  // Reportes Intermedios
  async getOrdenesPorVehiculoReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/3/ordenes-por-vehiculo');
    return response.data;
  },

  async getFacturasPendientesReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/4/facturas-pendientes');
    return response.data;
  },

  async getMecanicosPorEspecialidadReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/5/mecanicos-por-especialidad');
    return response.data;
  },

  async getHistorialServiciosReport(placa: string): Promise<ReportData[]> {
    const response = await apiClient.get(`/reports/6/historial-servicios/${placa}`);
    return response.data;
  },

  // Reportes Complejos
  async getIngresosMensualesReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/7/ingresos-mensuales');
    return response.data;
  },

  async getRendimientoMecanicosReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/8/rendimiento-mecanicos');
    return response.data;
  },

  async getRepuestosMasUtilizadosReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/9/repuestos-mas-utilizados');
    return response.data;
  },

  async getFacturacionAnualReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/10/facturacion-anual');
    return response.data;
  },

  // Reportes con subconsultas
  async getClientesVIPReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/11/clientes-vip');
    return response.data;
  },

  async getVehiculosMasServiciosReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/12/vehiculos-mas-servicios');
    return response.data;
  },

  async getFacturasSuperioresPromedioReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/13/facturas-superiores-promedio');
    return response.data;
  },

  async getHistorialClienteReport(documentoCliente: string): Promise<ReportData[]> {
    const response = await apiClient.get(`/reports/14/historial-cliente/${documentoCliente}`);
    return response.data;
  },

  async getServiciosDisponiblesReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/16/servicios-disponibles');
    return response.data;
  },

  async getRepuestosInventarioReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/17/repuestos-inventario');
    return response.data;
  },

  async getProveedoresActivosReport(): Promise<ReportData[]> {
    const response = await apiClient.get('/reports/18/proveedores-activos');
    return response.data;
  },

  // Exportación a PDF
  async exportReportToPDF(reportId: number, documentoCliente?: string): Promise<Blob> {
    let url = `/reports/export/${reportId}`;
    if (reportId === 14 && documentoCliente) {
      url = `/reports/export-historial/${documentoCliente}`;
      console.log('Exportando historial de cliente. URL:', url);
    } else if (reportId === 14 && !documentoCliente) {
      console.error('Error: Se intentó exportar historial sin documento de cliente');
      throw new Error('Se requiere el documento del cliente para exportar el historial');
    }
    console.log('URL de exportación:', url);
    const response = await apiClient.get(url, {
      responseType: 'blob',
    });
    return response.data;
  },
};