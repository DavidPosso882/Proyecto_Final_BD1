import React, { useState, useEffect } from 'react';
import DataTable from '../components/DataTable.jsx';
import Modal from '../components/Modal.jsx';

const FacturasPage = () => {
  const [facturas, setFacturas] = useState([]);
  const [ordenes, setOrdenes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingFactura, setEditingFactura] = useState(null);
  const [formData, setFormData] = useState({
    idOrdenTrabajo: '',
    fechaEmision: '',
    subtotal: '',
    impuestos: '',
    total: '',
    estado: 'PENDIENTE'
  });

  useEffect(() => {
    fetchFacturas();
    fetchOrdenes();
  }, []);

  const fetchFacturas = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/facturas');
      if (!response.ok) throw new Error('Error al cargar facturas');
      const data = await response.json();
      setFacturas(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchOrdenes = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/ordenes-trabajo');
      if (!response.ok) throw new Error('Error al cargar órdenes');
      const data = await response.json();
      setOrdenes(data);
    } catch (err) {
      console.error('Error cargando órdenes:', err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const url = editingFactura
        ? `http://localhost:8080/api/facturas/${editingFactura.idFactura}`
        : 'http://localhost:8080/api/facturas';

      const method = editingFactura ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...formData,
          idOrdenTrabajo: parseInt(formData.idOrdenTrabajo),
          subtotal: parseFloat(formData.subtotal),
          impuestos: parseFloat(formData.impuestos),
          total: parseFloat(formData.total),
          fechaEmision: formData.fechaEmision || new Date().toISOString().split('T')[0]
        })
      });

      if (!response.ok) throw new Error('Error al guardar factura');

      await fetchFacturas();
      setIsModalOpen(false);
      resetForm();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleEdit = (factura) => {
    setEditingFactura(factura);
    setFormData({
      idOrdenTrabajo: factura.idOrdenTrabajo.toString(),
      fechaEmision: factura.fechaEmision ? factura.fechaEmision.split('T')[0] : '',
      subtotal: factura.subtotal.toString(),
      impuestos: factura.impuestos.toString(),
      total: factura.total.toString(),
      estado: factura.estado
    });
    setIsModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Estás seguro de eliminar esta factura?')) return;

    try {
      const response = await fetch(`http://localhost:8080/api/facturas/${id}`, {
        method: 'DELETE'
      });

      if (!response.ok) throw new Error('Error al eliminar factura');

      await fetchFacturas();
    } catch (err) {
      setError(err.message);
    }
  };

  const resetForm = () => {
    setFormData({
      idOrdenTrabajo: '',
      fechaEmision: '',
      subtotal: '',
      impuestos: '',
      total: '',
      estado: 'PENDIENTE'
    });
    setEditingFactura(null);
  };

  const openCreateModal = () => {
    resetForm();
    setIsModalOpen(true);
  };

  const calculateTotal = () => {
    const subtotal = parseFloat(formData.subtotal) || 0;
    const impuestos = parseFloat(formData.impuestos) || 0;
    return (subtotal + impuestos).toFixed(2);
  };

  const columns = [
    { key: 'idFactura', label: 'ID' },
    {
      key: 'ordenTrabajo',
      label: 'Orden',
      render: (item) => item.ordenTrabajo?.codigo || 'N/A'
    },
    {
      key: 'fechaEmision',
      label: 'Fecha Emisión',
      render: (item) => item.fechaEmision ? new Date(item.fechaEmision).toLocaleDateString() : 'N/A'
    },
    {
      key: 'subtotal',
      label: 'Subtotal',
      render: (item) => `$${item.subtotal.toLocaleString()}`
    },
    {
      key: 'impuestos',
      label: 'Impuestos',
      render: (item) => `$${item.impuestos.toLocaleString()}`
    },
    {
      key: 'total',
      label: 'Total',
      render: (item) => `$${item.total.toLocaleString()}`
    },
    { key: 'estado', label: 'Estado' }
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
          Cargando facturas...
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
            Gestión de Facturas
          </h2>
          <p style={{ color: '#6b7280', marginTop: '4px' }}>
            Administra las facturas del taller
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
          ➕ Nueva Factura
        </button>
      </div>

      <DataTable
        data={facturas}
        columns={columns}
        title="Lista de Facturas"
        onEdit={handleEdit}
        onDelete={handleDelete}
      />

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingFactura ? 'Editar Factura' : 'Nueva Factura'}
        size="lg"
      >
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Orden de Trabajo *
              </label>
              <select
                value={formData.idOrdenTrabajo}
                onChange={(e) => setFormData({...formData, idOrdenTrabajo: e.target.value})}
                required
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
                }}
              >
                <option value="">Seleccionar orden</option>
                {ordenes.map(orden => (
                  <option key={orden.idOrdenTrabajo} value={orden.idOrdenTrabajo}>
                    {orden.codigo} - {orden.vehiculo?.marca} {orden.vehiculo?.modelo}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Fecha Emisión *
              </label>
              <input
                type="date"
                value={formData.fechaEmision}
                onChange={(e) => setFormData({...formData, fechaEmision: e.target.value})}
                required
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
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
                <option value="PAGADA">Pagada</option>
                <option value="CANCELADA">Cancelada</option>
                <option value="VENCIDA">Vencida</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Subtotal *
              </label>
              <input
                type="number"
                value={formData.subtotal}
                onChange={(e) => setFormData({...formData, subtotal: e.target.value})}
                required
                min="0"
                step="0.01"
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
                }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Impuestos *
              </label>
              <input
                type="number"
                value={formData.impuestos}
                onChange={(e) => setFormData({...formData, impuestos: e.target.value})}
                required
                min="0"
                step="0.01"
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
                }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Total
              </label>
              <input
                type="number"
                value={calculateTotal()}
                readOnly
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
                  backgroundColor: '#f9fafb',
                  fontWeight: '500',
                }}
              />
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
              {editingFactura ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default FacturasPage;