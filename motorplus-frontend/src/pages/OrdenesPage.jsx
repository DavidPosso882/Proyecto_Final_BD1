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
  const [editingOrden, setEditingOrden] = useState(null);
  const [formData, setFormData] = useState({
    codigo: '',
    descripcion: '',
    estado: 'PENDIENTE',
    idCliente: '',
    placaVehiculo: '',
    mecanicos: [],
    servicios: [],
    repuestos: []
  });

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
      const response = await fetch('http://localhost:8080/api/ordenes-trabajo');
      if (!response.ok) throw new Error('Error al cargar órdenes');
      const data = await response.json();
      setOrdenes(data);
    } catch (err) {
      setError(err.message);
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
        ? `http://localhost:8080/api/ordenes-trabajo/${editingOrden.idOrdenTrabajo}`
        : 'http://localhost:8080/api/ordenes-trabajo';

      const method = editingOrden ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...formData,
          placa: formData.placaVehiculo
        })
      });

      if (!response.ok) throw new Error('Error al guardar orden');

      await fetchOrdenes();
      setIsModalOpen(false);
      resetForm();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleEdit = (orden) => {
    setEditingOrden(orden);
    setFormData({
      codigo: orden.codigo,
      descripcion: orden.descripcion || '',
      estado: orden.estado,
      idCliente: orden.idCliente.toString(),
      placaVehiculo: orden.vehiculo?.placa || '',
      mecanicos: orden.mecanicos?.map(m => m.idMecanico) || [],
      servicios: orden.servicios?.map(s => s.idServicio) || [],
      repuestos: orden.repuestos?.map(r => ({ idRepuesto: r.idRepuesto, cantidad: r.cantidad })) || []
    });
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

  const handleServicioChange = (servicioId, checked) => {
    setFormData(prev => ({
      ...prev,
      servicios: checked
        ? [...prev.servicios, servicioId]
        : prev.servicios.filter(id => id !== servicioId)
    }));
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
        const cliente = clientes.find(c => c.idCliente === item.idCliente);
        return cliente ? `${cliente.nombre} ${cliente.apellido || ''}` : 'N/A';
      }
    },
    {
      key: 'vehiculoInfo',
      label: 'Vehículo',
      render: (item) => item.vehiculo ? `${item.vehiculo.marca} ${item.vehiculo.modelo}` : 'N/A'
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
    </div>
  );
};

export default OrdenesPage;