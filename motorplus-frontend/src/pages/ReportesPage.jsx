import React, { useState } from 'react';
import { reporteService } from '../services/reporteService';
import DataTable from '../components/DataTable';

const ReportesPage = () => {
  const [reportData, setReportData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentReport, setCurrentReport] = useState('');
  const [currentDocumentoCliente, setCurrentDocumentoCliente] = useState(null);

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
    ]},
    { id: 11, name: 'Clientes VIP (Gasto > Promedio)', endpoint: 'clientes-vip', columns: [
      { key: 'id', label: 'ID' },
      { key: 'nombre', label: 'Nombre' },
      { key: 'tipo', label: 'Tipo' },
      { key: 'gasto_total', label: 'Gasto Total' }
    ]},
    { id: 12, name: 'Vehículos Más Atendidos', endpoint: 'vehiculos-mas-servicios', columns: [
      { key: 'placa', label: 'Placa' },
      { key: 'vehiculo', label: 'Vehículo' },
      { key: 'cliente', label: 'Cliente' },
      { key: 'total_ordenes', label: 'Total Órdenes' }
    ]},
    { id: 13, name: 'Facturas Superiores al Promedio', endpoint: 'facturas-superiores-promedio', columns: [
      { key: 'id', label: 'ID' },
      { key: 'fecha', label: 'Fecha' },
      { key: 'total', label: 'Total' },
      { key: 'cliente', label: 'Cliente' }
    ]},
    { id: 14, name: 'Historial Completo de Cliente', endpoint: 'historial-cliente', columns: [
      { key: 'cliente', label: 'Cliente' },
      { key: 'placa', label: 'Placa' },
      { key: 'codigo_orden', label: 'Orden' },
      { key: 'fecha_ingreso', label: 'Fecha' },
      { key: 'estado', label: 'Estado' },
      { key: 'servicios', label: 'Servicios' },
      { key: 'total_servicios', label: 'Total Servicios' },
      { key: 'total_repuestos', label: 'Total Repuestos' },
      { key: 'costo_total', label: 'Total General' }
    ], requiresInput: true, inputLabel: 'Documento del Cliente' }
  ];

  const loadReport = async (reportId, endpoint, documentoCliente = null) => {
    try {
      setLoading(true);
      setError(null);
      setCurrentReport(reports.find(r => r.id === reportId)?.name || '');
      setCurrentDocumentoCliente(documentoCliente); // Guardar el documento del cliente

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
        case 11:
          data = await reporteService.getClientesVIPReport();
          break;
        case 12:
          data = await reporteService.getVehiculosMasServiciosReport();
          break;
        case 13:
          data = await reporteService.getFacturasSuperioresPromedioReport();
          break;
        case 14:
          if (!documentoCliente) {
            throw new Error('Se requiere el documento del cliente');
          }
          data = await reporteService.getHistorialClienteReport(documentoCliente);
          console.log('Historial de cliente recibido:', data);
          console.log('Cantidad de registros:', data ? data.length : 0);
          break;
        default:
          data = [];
      }

      console.log('Datos finales a mostrar:', data);
      console.log('Cantidad de registros:', Array.isArray(data) ? data.length : 'No es array');
      
      setReportData(data);
    } catch (err) {
      setError('Error al cargar el reporte: ' + err.message);
      console.error('Error completo:', err);
    } finally {
      setLoading(false);
    }
  };

  const exportToPDF = async (reportId, documentoCliente = null) => {
    try {
      // Usar el documento pasado como parámetro o el guardado en el estado
      const docCliente = documentoCliente || currentDocumentoCliente;
      
      const blob = await reporteService.exportReportToPDF(reportId, docCliente);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = reportId === 14 && docCliente 
        ? `historial_cliente_${docCliente}.pdf`
        : `reporte_${reportId}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setError('Error al exportar el PDF: ' + err.message);
      console.error('Error exportando PDF:', err);
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
                {report.requiresInput ? (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    <input
                      type="text"
                      placeholder={report.inputLabel || 'Ingrese el valor'}
                      id={`input-${report.id}`}
                      style={{
                        padding: '8px 12px',
                        border: '1px solid #d1d5db',
                        borderRadius: '6px',
                        fontSize: '0.875rem',
                        width: '100%',
                        boxSizing: 'border-box'
                      }}
                    />
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
                      onClick={() => {
                        const inputValue = document.getElementById(`input-${report.id}`).value;
                        if (!inputValue.trim()) {
                          alert('Por favor ingrese el documento del cliente');
                          return;
                        }
                        loadReport(report.id, report.endpoint, inputValue);
                      }}
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
                  </div>
                ) : (
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
                )}
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
                  onClick={() => {
                    if (report.requiresInput) {
                      const inputValue = document.getElementById(`input-${report.id}`).value;
                      if (!inputValue.trim()) {
                        alert('Por favor ingrese el documento del cliente primero');
                        return;
                      }
                      exportToPDF(report.id, inputValue);
                    } else {
                      exportToPDF(report.id);
                    }
                  }}
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

      {currentReport && !loading && !error && (
        <>
          {reportData.length > 0 ? (
            currentReportConfig && (
              <DataTable
                data={reportData}
                columns={currentReportConfig.columns}
                title={currentReport}
              />
            )
          ) : (
            <div style={{
              backgroundColor: '#fef3c7',
              border: '1px solid #fbbf24',
              borderRadius: '12px',
              padding: '16px',
              margin: '16px 0'
            }}>
              <div>
                <h4 style={{ color: '#92400e', marginBottom: '8px' }}>No hay registros</h4>
                <p style={{ color: '#78350f' }}>No se encontraron datos para este reporte con los parámetros proporcionados.</p>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default ReportesPage;