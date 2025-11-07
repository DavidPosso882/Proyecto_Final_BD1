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

  // Exportación a PDF
  async exportReportToPDF(reportId: number): Promise<Blob> {
    const response = await apiClient.get(`/reports/export/${reportId}`, {
      responseType: 'blob',
    });
    return response.data;
  },
};