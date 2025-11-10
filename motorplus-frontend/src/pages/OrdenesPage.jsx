import React, { useState, useEffect } from 'react';
import DataTable from '../components/DataTable.jsx';
import Modal from '../components/Modal.jsx';

const OrdenesPage = () => {
  const [ordenes, setOrdenes] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [vehiculos, setVehiculos] = useState([]);
  const [mecanicos, setMecanicos] = useState([]);
  const [servicios, setServicios] = useState([]);
  const [repuestos, setRepuestos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDetalleModalOpen, setIsDetalleModalOpen] = useState(false);
  const [ordenDetalle, setOrdenDetalle] = useState(null);
  const [editingOrden, setEditingOrden] = useState(null);
  const [formData, setFormData] = useState({
    codigo: '',
    descripcion: '',
    estado: 'PENDIENTE',
    idCliente: '',
    placaVehiculo: '',
    mecanicos: [],
    servicios: [], // Array de { servicioCodigo, cantidad, precioAplicado }
    repuestos: []
  });
  const [selectedServicios, setSelectedServicios] = useState([]); // IDs de servicios seleccionados

  useEffect(() => {
    fetchOrdenes();
    fetchClientes();
    fetchVehiculos();
    fetchMecanicos();
    fetchServicios();
    fetchRepuestos();
  }, []);

  const fetchOrdenes = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await fetch('http://localhost:8080/api/ordenes-trabajo');
      if (!response.ok) {
        throw new Error(`Error al cargar órdenes: ${response.status} ${response.statusText}`);
      }
      const data = await response.json();
      setOrdenes(data);
    } catch (err) {
      setError(err.message);
      console.error('Error fetching ordenes:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchClientes = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/clientes');
      if (!response.ok) throw new Error('Error al cargar clientes');
      const data = await response.json();
      setClientes(data);
    } catch (err) {
      console.error('Error cargando clientes:', err);
    }
  };

  const fetchVehiculos = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/vehiculos');
      if (!response.ok) throw new Error('Error al cargar vehículos');
      const data = await response.json();
      setVehiculos(data);
    } catch (err) {
      console.error('Error cargando vehículos:', err);
    }
  };

  const fetchMecanicos = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/mecanicos');
      if (!response.ok) throw new Error('Error al cargar mecánicos');
      const data = await response.json();
      setMecanicos(data);
    } catch (err) {
      console.error('Error cargando mecánicos:', err);
    }
  };

  const fetchServicios = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/servicios');
      if (!response.ok) throw new Error('Error al cargar servicios');
      const data = await response.json();
      setServicios(data);
    } catch (err) {
      console.error('Error cargando servicios:', err);
    }
  };

  const fetchRepuestos = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/repuestos');
      if (!response.ok) throw new Error('Error al cargar repuestos');
      const data = await response.json();
      setRepuestos(data);
    } catch (err) {
      console.error('Error cargando repuestos:', err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const url = editingOrden
        ? `http://localhost:8080/api/ordenes-trabajo/${editingOrden.codigo}`
        : 'http://localhost:8080/api/ordenes-trabajo';

      const method = editingOrden ? 'PUT' : 'POST';

      // Preparar los servicios seleccionados con la estructura correcta
      const serviciosData = selectedServicios.map(codigoServicio => {
        const servicio = servicios.find(s => s.codigo === codigoServicio);
        return {
          servicioCodigo: codigoServicio,
          cantidad: 1, // Puedes agregar un campo para cantidad si lo necesitas
          precioAplicado: servicio ? servicio.precio : 0
        };
      });

      const submitData = {
        placa: formData.placaVehiculo,
        diagnosticoInicial: formData.descripcion,
        estado: formData.estado,
        fechaIngreso: new Date().toISOString().split('T')[0], // Current date
        servicios: serviciosData
      };

      console.log('Submitting orden data:', submitData);

      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(submitData)
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.error || `Error al guardar orden: ${response.status} ${response.statusText}`);
      }

      const savedOrden = await response.json();
      console.log('Orden guardada:', savedOrden);

      await fetchOrdenes();
      setIsModalOpen(false);
      resetForm();
    } catch (err) {
      setError(err.message);
      console.error('Error saving orden:', err);
    }
  };

  const handleEdit = (orden) => {
    setEditingOrden(orden);
    setFormData({
      codigo: orden.codigo || '',
      descripcion: orden.diagnosticoInicial || orden.descripcion || '',
      estado: orden.estado || 'PENDIENTE',
      idCliente: orden.cliente?.idCliente || orden.idCliente?.toString() || '',
      placaVehiculo: orden.vehiculo?.placa || orden.placa || '',
      mecanicos: orden.mecanicos?.map(m => m.idMecanico) || [],
      servicios: orden.servicios || [],
      repuestos: orden.repuestos?.map(r => ({ idRepuesto: r.idRepuesto, cantidad: r.cantidadUsada || r.cantidad })) || []
    });
    
    // Cargar los servicios seleccionados
    const serviciosSeleccionados = orden.servicios?.map(s => s.servicioCodigo || s.servicio?.codigo) || [];
    setSelectedServicios(serviciosSeleccionados);
    
    setIsModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Estás seguro de eliminar esta orden de trabajo?')) return;

    try {
      const response = await fetch(`http://localhost:8080/api/ordenes-trabajo/${id}`, {
        method: 'DELETE'
      });

      if (!response.ok) throw new Error('Error al eliminar orden');

      await fetchOrdenes();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleVerDetalle = async (ordenCodigo) => {
    try {
      setLoading(true);
      setError(null);
      
      const response = await fetch(`http://localhost:8080/api/reports/15/orden-trabajo/${ordenCodigo}`);
      
      if (!response.ok) {
        throw new Error(`Error ${response.status}: No se pudo cargar el detalle de la orden`);
      }
      
      const detalle = await response.json();
      
      // Verificar si hay error en la respuesta
      if (detalle.error) {
        throw new Error(detalle.mensaje || 'Error al cargar el detalle de la orden');
      }
      
      setOrdenDetalle(detalle);
      setIsDetalleModalOpen(true);
    } catch (err) {
      setError('Error al cargar el detalle: ' + err.message);
      console.error('Error cargando detalle de orden:', err);
      alert('Error al cargar el detalle de la orden. Por favor, verifica que:\n- El backend esté corriendo\n- La orden tenga datos completos\n- La conexión a la base de datos esté activa');
    } finally {
      setLoading(false);
    }
  };

  const handleExportarPDF = async (ordenCodigo) => {
    try {
      const response = await fetch(`http://localhost:8080/api/reports/export/orden-trabajo/${ordenCodigo}`);
      
      if (!response.ok) throw new Error('Error al generar el PDF');
      
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `orden_trabajo_${ordenCodigo}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setError('Error al exportar el PDF: ' + err.message);
      console.error('Error exportando PDF:', err);
    }
  };

  const resetForm = () => {
    setFormData({
      codigo: '',
      descripcion: '',
      estado: 'PENDIENTE',
      idCliente: '',
      placaVehiculo: '',
      mecanicos: [],
      servicios: [],
      repuestos: []
    });
    setSelectedServicios([]);
    setEditingOrden(null);
  };

  const openCreateModal = () => {
    resetForm();
    setIsModalOpen(true);
  };

  const handleMecanicoChange = (mecanicoId, checked) => {
    setFormData(prev => ({
      ...prev,
      mecanicos: checked
        ? [...prev.mecanicos, mecanicoId]
        : prev.mecanicos.filter(id => id !== mecanicoId)
    }));
  };

  const handleServicioChange = (servicioCodigo, checked) => {
    if (checked) {
      setSelectedServicios(prev => [...prev, servicioCodigo]);
    } else {
      setSelectedServicios(prev => prev.filter(id => id !== servicioCodigo));
    }
  };

  const handleRepuestoChange = (repuestoId, cantidad) => {
    setFormData(prev => ({
      ...prev,
      repuestos: cantidad > 0
        ? [...prev.repuestos.filter(r => r.idRepuesto !== repuestoId), { idRepuesto: repuestoId, cantidad }]
        : prev.repuestos.filter(r => r.idRepuesto !== repuestoId)
    }));
  };

  const columns = [
    { key: 'codigo', label: 'Código' },
    { key: 'estado', label: 'Estado' },
    {
      key: 'clienteNombre',
      label: 'Cliente',
      render: (item) => {
        return item.cliente ? `${item.cliente.nombre} ${item.cliente.apellido || ''}` : 'N/A';
      }
    },
    {
      key: 'vehiculoInfo',
      label: 'Vehículo',
      render: (item) => {
        return item.vehiculo ? `${item.vehiculo.marca} ${item.vehiculo.modelo}` : 'N/A';
      }
    },
    {
      key: 'serviciosCount',
      label: 'Servicios',
      render: (item) => item.servicios?.length || 0
    },
    {
      key: 'mecanicosCount',
      label: 'Mecánicos',
      render: (item) => item.mecanicos?.length || 0
    },
    {
      key: 'fechaCreacion',
      label: 'Fecha Creación',
      render: (item) => item.fechaCreacion ? new Date(item.fechaCreacion).toLocaleDateString() : 'N/A'
    }
  ];

  if (loading) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '200px' }}>
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
          Cargando órdenes de trabajo...
        </p>
        <style>{`
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
        `}</style>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{
        backgroundColor: '#fef2f2',
        border: '1px solid #fecaca',
        borderRadius: '12px',
        padding: '16px',
        margin: '16px 0'
      }}>
        <div>
          <h4 style={{ color: '#dc2626', marginBottom: '8px' }}>Error al cargar datos</h4>
          <p style={{ color: '#7f1d1d' }}>{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '1.875rem', fontWeight: 'bold', color: '#1f2937' }}>
            Órdenes de Trabajo
          </h2>
          <p style={{ color: '#6b7280', marginTop: '4px' }}>
            Gestiona las órdenes de trabajo y mantenimiento
          </p>
        </div>
        <button
          onClick={openCreateModal}
          style={{
            backgroundColor: '#2563eb',
            color: 'white',
            padding: '12px 24px',
            border: 'none',
            borderRadius: '8px',
            fontSize: '1rem',
            fontWeight: '500',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            transition: 'all 0.2s',
          }}
          onMouseEnter={(e) => {
            e.target.style.backgroundColor = '#1d4ed8';
            e.target.style.transform = 'translateY(-1px)';
            e.target.style.boxShadow = '0 10px 15px -3px rgba(0, 0, 0, 0.1)';
          }}
          onMouseLeave={(e) => {
            e.target.style.backgroundColor = '#2563eb';
            e.target.style.transform = 'translateY(0)';
            e.target.style.boxShadow = 'none';
          }}
        >
          ➕ Nueva Orden
        </button>
      </div>

      <DataTable
        data={ordenes}
        columns={columns}
        title="Lista de Órdenes de Trabajo"
        onView={(orden) => handleVerDetalle(orden.codigo)}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingOrden ? 'Editar Orden de Trabajo' : 'Nueva Orden de Trabajo'}
        size="xl"
      >
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Código *
              </label>
              <input
                type="text"
                value={formData.codigo}
                onChange={(e) => setFormData({...formData, codigo: e.target.value})}
                required
                disabled={!!editingOrden}
                pattern="[A-Za-z0-9\-]+"
                title="Solo letras, números y guiones permitidos"
                placeholder="ORD001"
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
                  backgroundColor: editingOrden ? '#f9fafb' : 'white',
                }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Estado *
              </label>
              <select
                value={formData.estado}
                onChange={(e) => setFormData({...formData, estado: e.target.value})}
                required
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
                }}
              >
                <option value="PENDIENTE">Pendiente</option>
                <option value="EN_PROCESO">En Proceso</option>
                <option value="COMPLETADA">Completada</option>
                <option value="CANCELADA">Cancelada</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Cliente *
              </label>
              <select
                value={formData.idCliente}
                onChange={(e) => setFormData({...formData, idCliente: e.target.value})}
                required
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
                }}
              >
                <option value="">Seleccionar cliente</option>
                {clientes.map(cliente => (
                  <option key={cliente.idCliente} value={cliente.idCliente}>
                    {cliente.nombre} {cliente.apellido || ''}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
              Descripción
            </label>
            <textarea
              value={formData.descripcion}
              onChange={(e) => setFormData({...formData, descripcion: e.target.value})}
              rows={3}
              style={{
                width: '100%',
                padding: '8px 12px',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                fontSize: '0.875rem',
                resize: 'vertical',
              }}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Vehículo
              </label>
              <select
                value={formData.placaVehiculo}
                onChange={(e) => setFormData({...formData, placaVehiculo: e.target.value})}
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
                }}
              >
                <option value="">Seleccionar vehículo</option>
                {vehiculos.map(vehiculo => (
                  <option key={vehiculo.placa} value={vehiculo.placa}>
                    {vehiculo.placa} - {vehiculo.marca} {vehiculo.modelo}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Sección de Servicios */}
          <div style={{ marginTop: '24px', padding: '16px', backgroundColor: '#f9fafb', borderRadius: '8px', border: '1px solid #e5e7eb' }}>
            <h3 style={{ fontSize: '1rem', fontWeight: '600', color: '#374151', marginBottom: '12px' }}>
              Servicios
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '12px', maxHeight: '300px', overflowY: 'auto', padding: '8px' }}>
              {servicios.length > 0 ? (
                servicios.map(servicio => (
                  <label
                    key={servicio.codigo}
                    style={{
                      display: 'flex',
                      alignItems: 'flex-start',
                      padding: '12px',
                      backgroundColor: 'white',
                      border: selectedServicios.includes(servicio.codigo) ? '2px solid #2563eb' : '1px solid #d1d5db',
                      borderRadius: '6px',
                      cursor: 'pointer',
                      transition: 'all 0.2s',
                    }}
                    onMouseEnter={(e) => {
                      if (!selectedServicios.includes(servicio.codigo)) {
                        e.currentTarget.style.borderColor = '#9ca3af';
                      }
                    }}
                    onMouseLeave={(e) => {
                      if (!selectedServicios.includes(servicio.codigo)) {
                        e.currentTarget.style.borderColor = '#d1d5db';
                      }
                    }}
                  >
                    <input
                      type="checkbox"
                      checked={selectedServicios.includes(servicio.codigo)}
                      onChange={(e) => handleServicioChange(servicio.codigo, e.target.checked)}
                      style={{
                        marginRight: '8px',
                        marginTop: '2px',
                        width: '16px',
                        height: '16px',
                        cursor: 'pointer',
                      }}
                    />
                    <div style={{ flex: 1 }}>
                      <div style={{ fontSize: '0.875rem', fontWeight: '600', color: '#111827', marginBottom: '4px' }}>
                        {servicio.nombre}
                      </div>
                      {servicio.descripcion && (
                        <div style={{ fontSize: '0.75rem', color: '#6b7280', marginBottom: '4px' }}>
                          {servicio.descripcion}
                        </div>
                      )}
                      {servicio.precio && (
                        <div style={{ fontSize: '0.875rem', fontWeight: '500', color: '#059669' }}>
                          ${parseFloat(servicio.precio).toFixed(2)}
                        </div>
                      )}
                    </div>
                  </label>
                ))
              ) : (
                <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '20px', color: '#6b7280' }}>
                  No hay servicios disponibles
                </div>
              )}
            </div>
            {selectedServicios.length > 0 && (
              <div style={{ marginTop: '12px', padding: '8px', backgroundColor: '#eff6ff', borderRadius: '6px' }}>
                <span style={{ fontSize: '0.875rem', color: '#1e40af', fontWeight: '500' }}>
                  {selectedServicios.length} servicio{selectedServicios.length !== 1 ? 's' : ''} seleccionado{selectedServicios.length !== 1 ? 's' : ''}
                </span>
              </div>
            )}
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '24px' }}>
            <button
              type="button"
              onClick={() => setIsModalOpen(false)}
              style={{
                padding: '8px 16px',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                backgroundColor: 'white',
                color: '#374151',
                fontSize: '0.875rem',
                cursor: 'pointer',
                transition: 'all 0.2s',
              }}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#f9fafb'}
              onMouseLeave={(e) => e.target.style.backgroundColor = 'white'}
            >
              Cancelar
            </button>
            <button
              type="submit"
              style={{
                padding: '8px 16px',
                border: 'none',
                borderRadius: '6px',
                backgroundColor: '#2563eb',
                color: 'white',
                fontSize: '0.875rem',
                fontWeight: '500',
                cursor: 'pointer',
                transition: 'all 0.2s',
              }}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#1d4ed8'}
              onMouseLeave={(e) => e.target.style.backgroundColor = '#2563eb'}
            >
              {editingOrden ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Modal de Detalle de Orden */}
      <Modal
        isOpen={isDetalleModalOpen}
        onClose={() => setIsDetalleModalOpen(false)}
        title="Detalle de Orden de Trabajo"
        size="xl"
      >
        {ordenDetalle && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
            {/* Información de la Orden */}
            <div style={{ padding: '16px', backgroundColor: '#f9fafb', borderRadius: '8px' }}>
              <h3 style={{ fontSize: '1.125rem', fontWeight: '600', color: '#111827', marginBottom: '12px' }}>
                Información de la Orden
              </h3>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div>
                  <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Código: </span>
                  <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                    {ordenDetalle.orden.codigo}
                  </span>
                </div>
                <div>
                  <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Estado: </span>
                  <span style={{
                    fontSize: '0.875rem',
                    fontWeight: '500',
                    padding: '2px 8px',
                    borderRadius: '4px',
                    backgroundColor: ordenDetalle.orden.estado === 'COMPLETADA' ? '#d1fae5' : '#fef3c7',
                    color: ordenDetalle.orden.estado === 'COMPLETADA' ? '#065f46' : '#92400e'
                  }}>
                    {ordenDetalle.orden.estado}
                  </span>
                </div>
                <div>
                  <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Fecha Ingreso: </span>
                  <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                    {new Date(ordenDetalle.orden.fecha_ingreso).toLocaleDateString()}
                  </span>
                </div>
                <div style={{ gridColumn: '1 / -1' }}>
                  <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Diagnóstico: </span>
                  <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                    {ordenDetalle.orden.diagnostico_inicial || 'N/A'}
                  </span>
                </div>
              </div>
            </div>

            {/* Información del Cliente */}
            <div style={{ padding: '16px', backgroundColor: '#eff6ff', borderRadius: '8px' }}>
              <h3 style={{ fontSize: '1.125rem', fontWeight: '600', color: '#111827', marginBottom: '12px' }}>
                Cliente
              </h3>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div>
                  <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Nombre: </span>
                  <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                    {ordenDetalle.orden.cliente_nombre}
                  </span>
                </div>
                <div>
                  <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Documento: </span>
                  <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                    {ordenDetalle.orden.cliente_documento}
                  </span>
                </div>
                <div>
                  <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Teléfono: </span>
                  <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                    {ordenDetalle.orden.cliente_telefono || 'N/A'}
                  </span>
                </div>
                <div>
                  <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Email: </span>
                  <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                    {ordenDetalle.orden.cliente_email || 'N/A'}
                  </span>
                </div>
              </div>
            </div>

            {/* Información del Vehículo */}
            <div style={{ padding: '16px', backgroundColor: '#fef3c7', borderRadius: '8px' }}>
              <h3 style={{ fontSize: '1.125rem', fontWeight: '600', color: '#111827', marginBottom: '12px' }}>
                Vehículo
              </h3>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div>
                  <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Placa: </span>
                  <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                    {ordenDetalle.orden.placa}
                  </span>
                </div>
                <div>
                  <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Marca/Modelo: </span>
                  <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                    {ordenDetalle.orden.vehiculo_marca} {ordenDetalle.orden.vehiculo_modelo}
                  </span>
                </div>
                <div>
                  <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Año: </span>
                  <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                    {ordenDetalle.orden.vehiculo_anio}
                  </span>
                </div>
                <div>
                  <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Tipo: </span>
                  <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                    {ordenDetalle.orden.vehiculo_tipo}
                  </span>
                </div>
              </div>
            </div>

            {/* Servicios */}
            {ordenDetalle.servicios && ordenDetalle.servicios.length > 0 && (
              <div style={{ padding: '16px', backgroundColor: '#f0fdf4', borderRadius: '8px' }}>
                <h3 style={{ fontSize: '1.125rem', fontWeight: '600', color: '#111827', marginBottom: '12px' }}>
                  Servicios Realizados
                </h3>
                <table style={{ width: '100%', fontSize: '0.875rem' }}>
                  <thead>
                    <tr style={{ borderBottom: '2px solid #d1d5db' }}>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Servicio</th>
                      <th style={{ padding: '8px', textAlign: 'center' }}>Cantidad</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Precio Unit.</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Subtotal</th>
                    </tr>
                  </thead>
                  <tbody>
                    {ordenDetalle.servicios.map((servicio, idx) => (
                      <tr key={idx} style={{ borderBottom: '1px solid #e5e7eb' }}>
                        <td style={{ padding: '8px' }}>{servicio.nombre}</td>
                        <td style={{ padding: '8px', textAlign: 'center' }}>{servicio.cantidad}</td>
                        <td style={{ padding: '8px', textAlign: 'right' }}>
                          ${parseFloat(servicio.precio_aplicado).toFixed(2)}
                        </td>
                        <td style={{ padding: '8px', textAlign: 'right', fontWeight: '500' }}>
                          ${parseFloat(servicio.subtotal).toFixed(2)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {/* Repuestos */}
            {ordenDetalle.repuestos && ordenDetalle.repuestos.length > 0 && (
              <div style={{ padding: '16px', backgroundColor: '#fef2f2', borderRadius: '8px' }}>
                <h3 style={{ fontSize: '1.125rem', fontWeight: '600', color: '#111827', marginBottom: '12px' }}>
                  Repuestos Utilizados
                </h3>
                <table style={{ width: '100%', fontSize: '0.875rem' }}>
                  <thead>
                    <tr style={{ borderBottom: '2px solid #d1d5db' }}>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Repuesto</th>
                      <th style={{ padding: '8px', textAlign: 'center' }}>Cantidad</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Precio Unit.</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Subtotal</th>
                    </tr>
                  </thead>
                  <tbody>
                    {ordenDetalle.repuestos.map((repuesto, idx) => (
                      <tr key={idx} style={{ borderBottom: '1px solid #e5e7eb' }}>
                        <td style={{ padding: '8px' }}>{repuesto.nombre}</td>
                        <td style={{ padding: '8px', textAlign: 'center' }}>{repuesto.cantidad_usada}</td>
                        <td style={{ padding: '8px', textAlign: 'right' }}>
                          ${parseFloat(repuesto.precio_aplicado).toFixed(2)}
                        </td>
                        <td style={{ padding: '8px', textAlign: 'right', fontWeight: '500' }}>
                          ${parseFloat(repuesto.subtotal).toFixed(2)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {/* Mecánicos */}
            {ordenDetalle.mecanicos && ordenDetalle.mecanicos.length > 0 && (
              <div style={{ padding: '16px', backgroundColor: '#f5f3ff', borderRadius: '8px' }}>
                <h3 style={{ fontSize: '1.125rem', fontWeight: '600', color: '#111827', marginBottom: '12px' }}>
                  Mecánicos Asignados
                </h3>
                <table style={{ width: '100%', fontSize: '0.875rem' }}>
                  <thead>
                    <tr style={{ borderBottom: '2px solid #d1d5db' }}>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Mecánico</th>
                      <th style={{ padding: '8px', textAlign: 'center' }}>Rol</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Horas Trabajadas</th>
                    </tr>
                  </thead>
                  <tbody>
                    {ordenDetalle.mecanicos.map((mecanico, idx) => (
                      <tr key={idx} style={{ borderBottom: '1px solid #e5e7eb' }}>
                        <td style={{ padding: '8px' }}>{mecanico.nombre}</td>
                        <td style={{ padding: '8px', textAlign: 'center' }}>
                          {mecanico.rol || 'N/A'}
                        </td>
                        <td style={{ padding: '8px', textAlign: 'right', fontWeight: '500' }}>
                          {mecanico.horas_trabajadas}h
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {/* Factura */}
            {ordenDetalle.factura && (
              <div style={{ padding: '16px', backgroundColor: '#e0e7ff', borderRadius: '8px' }}>
                <h3 style={{ fontSize: '1.125rem', fontWeight: '600', color: '#111827', marginBottom: '12px' }}>
                  Factura
                </h3>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                  <div>
                    <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>ID Factura: </span>
                    <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                      {ordenDetalle.factura.id_factura}
                    </span>
                  </div>
                  <div>
                    <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Estado: </span>
                    <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                      {ordenDetalle.factura.estado_pago}
                    </span>
                  </div>
                  <div>
                    <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Subtotal: </span>
                    <span style={{ fontSize: '0.875rem', fontWeight: '500', color: '#111827' }}>
                      ${parseFloat(ordenDetalle.factura.subtotal).toFixed(2)}
                    </span>
                  </div>
                  <div>
                    <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Total: </span>
                    <span style={{ fontSize: '1rem', fontWeight: '700', color: '#059669' }}>
                      ${parseFloat(ordenDetalle.factura.total).toFixed(2)}
                    </span>
                  </div>
                </div>
              </div>
            )}

            {/* Botones de acción */}
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '12px' }}>
              <button
                type="button"
                onClick={() => handleExportarPDF(ordenDetalle.orden.codigo)}
                style={{
                  padding: '10px 20px',
                  border: '1px solid #16a34a',
                  borderRadius: '8px',
                  backgroundColor: 'white',
                  color: '#16a34a',
                  fontSize: '0.875rem',
                  fontWeight: '500',
                  cursor: 'pointer',
                  transition: 'all 0.2s',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#16a34a';
                  e.target.style.color = 'white';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'white';
                  e.target.style.color = '#16a34a';
                }}
              >
                📄 Exportar a PDF
              </button>
              <button
                type="button"
                onClick={() => setIsDetalleModalOpen(false)}
                style={{
                  padding: '10px 20px',
                  border: '1px solid #d1d5db',
                  borderRadius: '8px',
                  backgroundColor: 'white',
                  color: '#374151',
                  fontSize: '0.875rem',
                  fontWeight: '500',
                  cursor: 'pointer',
                  transition: 'all 0.2s',
                }}
                onMouseEnter={(e) => e.target.style.backgroundColor = '#f9fafb'}
                onMouseLeave={(e) => e.target.style.backgroundColor = 'white'}
              >
                Cerrar
              </button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default OrdenesPage;