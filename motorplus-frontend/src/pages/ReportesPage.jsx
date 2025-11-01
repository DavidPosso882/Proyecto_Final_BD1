import React, { useState } from 'react';
import DataTable from '../components/DataTable';

const ReportesPage = () => {
  const [reportData, setReportData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentReport, setCurrentReport] = useState('');

  const reports = [
    { id: 1, name: 'Lista de Clientes', endpoint: 'clientes', columns: [
      { key: 'id', label: 'ID' },
      { key: 'nombre', label: 'Nombre' },
      { key: 'telefono', label: 'Teléfono' },
      { key: 'tipo', label: 'Tipo' }
    ]},
    { id: 2, name: 'Lista de Vehículos', endpoint: 'vehiculos', columns: [
      { key: 'placa', label: 'Placa' },
      { key: 'marca', label: 'Marca' },
      { key: 'modelo', label: 'Modelo' },
      { key: 'anio', label: 'Año' }
    ]},
    { id: 3, name: 'Órdenes de Trabajo', endpoint: 'ordenes-trabajo', columns: [
      { key: 'codigo', label: 'Código' },
      { key: 'placa', label: 'Placa' },
      { key: 'estado', label: 'Estado' }
    ]},
    { id: 4, name: 'Facturas Pendientes', endpoint: 'facturas-pendientes', columns: [
      { key: 'id', label: 'ID' },
      { key: 'fecha', label: 'Fecha' },
      { key: 'total', label: 'Total' }
    ]},
    { id: 5, name: 'Mecánicos por Especialidad', endpoint: 'mecanicos-por-especialidad', columns: [
      { key: 'id', label: 'ID' },
      { key: 'nombre', label: 'Nombre' },
      { key: 'telefono', label: 'Teléfono' }
    ]},
    { id: 7, name: 'Ingresos Mensuales', endpoint: 'ingresos-mensuales', columns: [
      { key: 'mes', label: 'Mes' },
      { key: 'total', label: 'Total' }
    ]},
    { id: 8, name: 'Rendimiento de Mecánicos', endpoint: 'rendimiento-mecanicos', columns: [
      { key: 'mecanico', label: 'Mecánico' },
      { key: 'ordenes_completadas', label: 'Órdenes Completadas' }
    ]},
    { id: 9, name: 'Repuestos Más Utilizados', endpoint: 'repuestos-mas-utilizados', columns: [
      { key: 'repuesto', label: 'Repuesto' },
      { key: 'usos', label: 'Usos' }
    ]},
    { id: 10, name: 'Facturación Anual', endpoint: 'facturacion-anual', columns: [
      { key: 'mes', label: 'Mes' },
      { key: 'facturacion', label: 'Facturación' }
    ]}
  ];

  const loadReport = async (reportId, endpoint) => {
    try {
      setLoading(true);
      setError(null);
      setCurrentReport(reports.find(r => r.id === reportId)?.name || '');

      let data;
      switch (reportId) {
        case 1:
          data = await reporteService.getClientesReport();
          break;
        case 2:
          data = await reporteService.getVehiculosReport();
          break;
        case 3:
          data = await reporteService.getOrdenesPorVehiculoReport();
          break;
        case 4:
          data = await reporteService.getFacturasPendientesReport();
          break;
        case 5:
          data = await reporteService.getMecanicosPorEspecialidadReport();
          break;
        case 7:
          data = await reporteService.getIngresosMensualesReport();
          break;
        case 8:
          data = await reporteService.getRendimientoMecanicosReport();
          break;
        case 9:
          data = await reporteService.getRepuestosMasUtilizadosReport();
          break;
        case 10:
          data = await reporteService.getFacturacionAnualReport();
          break;
        default:
          data = [];
      }

      setReportData(data);
    } catch (err) {
      setError('Error al cargar el reporte');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const exportToPDF = async (reportId) => {
    try {
      const blob = await reporteService.exportReportToPDF(reportId);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `reporte_${reportId}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setError('Error al exportar el PDF');
      console.error(err);
    }
  };

  const currentReportConfig = reports.find(r => r.name === currentReport);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div>
        <h2 style={{ fontSize: '1.875rem', fontWeight: 'bold', color: '#1f2937' }}>
          Reportes del Sistema MotorPlus
        </h2>
        <p style={{ color: '#6b7280', marginTop: '4px' }}>
          Genera y visualiza reportes del sistema
        </p>
      </div>

      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
        gap: '24px'
      }}>
        {reports.map((report) => (
          <div
            key={report.id}
            style={{
              backgroundColor: 'white',
              borderRadius: '12px',
              boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
              overflow: 'hidden',
              transition: 'all 0.2s',
              cursor: 'pointer',
            }}
            onMouseEnter={(e) => {
              e.target.style.transform = 'translateY(-2px)';
              e.target.style.boxShadow = '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)';
            }}
            onMouseLeave={(e) => {
              e.target.style.transform = 'translateY(0)';
              e.target.style.boxShadow = '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)';
            }}
          >
            <div style={{ padding: '20px', paddingBottom: '12px' }}>
              <h3 style={{ fontSize: '1.125rem', fontWeight: '600', color: '#1f2937' }}>
                {report.name}
              </h3>
            </div>
            <div style={{ padding: '0 20px 20px 20px' }}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                <button
                  style={{
                    backgroundColor: '#c60f0fff',
                    color: 'white',
                    padding: '10px 16px',
                    border: 'none',
                    borderRadius: '8px',
                    fontSize: '0.875rem',
                    fontWeight: '500',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '8px',
                    transition: 'all 0.2s',
                    width: '100%',
                  }}
                  onClick={() => loadReport(report.id, report.endpoint)}
                  disabled={loading}
                  onMouseEnter={(e) => {
                    if (!loading) {
                      e.target.style.backgroundColor = '#1d4ed8';
                      e.target.style.transform = 'translateY(-1px)';
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (!loading) {
                      e.target.style.backgroundColor = '#2563eb';
                      e.target.style.transform = 'translateY(0)';
                    }
                  }}
                >
                  👁️ Ver Reporte
                </button>
                <button
                  style={{
                    backgroundColor: 'transparent',
                    color: '#16a34a',
                    padding: '10px 16px',
                    border: '1px solid #16a34a',
                    borderRadius: '8px',
                    fontSize: '0.875rem',
                    fontWeight: '500',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '8px',
                    transition: 'all 0.2s',
                    width: '100%',
                  }}
                  onClick={() => exportToPDF(report.id)}
                  disabled={loading}
                  onMouseEnter={(e) => {
                    if (!loading) {
                      e.target.style.backgroundColor = '#16a34a';
                      e.target.style.color = 'white';
                      e.target.style.transform = 'translateY(-1px)';
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (!loading) {
                      e.target.style.backgroundColor = 'transparent';
                      e.target.style.color = '#16a34a';
                      e.target.style.transform = 'translateY(0)';
                    }
                  }}
                >
                  📄 Exportar PDF
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {error && (
        <div style={{
          backgroundColor: '#fef2f2',
          border: '1px solid #fecaca',
          borderRadius: '12px',
          padding: '16px',
          margin: '16px 0'
        }}>
          <div>
            <h4 style={{ color: '#dc2626', marginBottom: '8px' }}>Error al cargar reporte</h4>
            <p style={{ color: '#7f1d1d' }}>{error}</p>
          </div>
        </div>
      )}

      {loading && (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '32px 0' }}>
          <div style={{
            width: '40px',
            height: '40px',
            border: '4px solid #e5e7eb',
            borderTop: '4px solid #2563eb',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite',
            marginBottom: '16px'
          }}></div>
          <p style={{ fontSize: '1.125rem', color: '#6b7280' }}>
            Cargando reporte...
          </p>
          <style>{`
            @keyframes spin {
              0% { transform: rotate(0deg); }
              100% { transform: rotate(360deg); }
            }
          `}</style>
        </div>
      )}

      {reportData.length > 0 && currentReportConfig && (
        <DataTable
          data={reportData}
          columns={currentReportConfig.columns}
          title={currentReport}
        />
      )}
    </div>
  );
};

export default ReportesPage;