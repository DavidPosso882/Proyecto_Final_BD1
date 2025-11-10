import React, { useState, useEffect } from 'react';
import DataTable from '../components/DataTable.jsx';
import Modal from '../components/Modal.jsx';

const RepuestosPage = () => {
  const [repuestos, setRepuestos] = useState([]);
  const [proveedores, setProveedores] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingRepuesto, setEditingRepuesto] = useState(null);
  const [formData, setFormData] = useState({
    nombre: '',
    descripcion: '',
    precio: '',
    stock: '',
    idProveedor: ''
  });

  useEffect(() => {
    fetchRepuestos();
    fetchProveedores();
  }, []);

  const fetchRepuestos = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/repuestos');
      if (!response.ok) throw new Error('Error al cargar repuestos');
      const data = await response.json();
      setRepuestos(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchProveedores = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/proveedores');
      if (!response.ok) throw new Error('Error al cargar proveedores');
      const data = await response.json();
      setProveedores(data);
    } catch (err) {
      console.error('Error cargando proveedores:', err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Obtener el ID del repuesto (puede ser idRepuesto o codigo)
      const repuestoId = editingRepuesto ? (editingRepuesto.idRepuesto || editingRepuesto.codigo) : null;
      
      const url = editingRepuesto
        ? `http://localhost:8080/api/repuestos/${repuestoId}`
        : 'http://localhost:8080/api/repuestos';

      const method = editingRepuesto ? 'PUT' : 'POST';

      const submitData = {
        nombre: formData.nombre,
        descripcion: formData.descripcion,
        precioUnitario: parseFloat(formData.precio),
        stock: parseInt(formData.stock),
        stockMinimo: parseInt(formData.stock) || 5,
        idProveedor: formData.idProveedor ? parseInt(formData.idProveedor) : null
      };

      console.log('Repuesto a editar:', editingRepuesto);
      console.log('ID del repuesto:', repuestoId);
      console.log('Enviando datos:', submitData);
      console.log('URL:', url);
      console.log('Método:', method);

      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(submitData)
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Error del servidor:', errorText);
        throw new Error('Error al guardar repuesto: ' + errorText);
      }

      await fetchRepuestos();
      setIsModalOpen(false);
      resetForm();
      setError(null);
    } catch (err) {
      console.error('Error completo:', err);
      setError(err.message);
    }
  };

  const handleEdit = (repuesto) => {
    setEditingRepuesto(repuesto);
    setFormData({
      nombre: repuesto.nombre,
      descripcion: repuesto.descripcion || '',
      precio: repuesto.precioUnitario.toString(),
      stock: repuesto.stock.toString(),
      idProveedor: repuesto.idProveedor ? repuesto.idProveedor.toString() : ''
    });
    setIsModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Estás seguro de eliminar este repuesto?')) return;

    try {
      const response = await fetch(`http://localhost:8080/api/repuestos/${id}`, {
        method: 'DELETE'
      });

      if (!response.ok) throw new Error('Error al eliminar repuesto');

      await fetchRepuestos();
    } catch (err) {
      setError(err.message);
    }
  };

  const resetForm = () => {
    setFormData({
      nombre: '',
      descripcion: '',
      precio: '',
      stock: '',
      idProveedor: ''
    });
    setEditingRepuesto(null);
  };

  const openCreateModal = () => {
    resetForm();
    setIsModalOpen(true);
  };

  const columns = [
    { key: 'idRepuesto', label: 'ID' },
    { key: 'nombre', label: 'Nombre' },
    { key: 'descripcion', label: 'Descripción' },
    {
      key: 'precioUnitario',
      label: 'Precio',
      render: (item) => `$${item.precioUnitario.toLocaleString()}`
    },
    { key: 'stock', label: 'Stock' },
    {
      key: 'proveedorNombre',
      label: 'Proveedor',
      render: (item) => {
        const proveedor = proveedores.find(p => p.id === item.idProveedor);
        return proveedor ? proveedor.nombre : 'N/A';
      }
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
          Cargando repuestos...
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
            Gestión de Repuestos
          </h2>
          <p style={{ color: '#6b7280', marginTop: '4px' }}>
            Administra el inventario de repuestos
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
          ➕ Nuevo Repuesto
        </button>
      </div>

      <DataTable
        data={repuestos}
        columns={columns}
        title="Lista de Repuestos"
        onEdit={handleEdit}
        onDelete={handleDelete}
      />

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingRepuesto ? 'Editar Repuesto' : 'Nuevo Repuesto'}
        size="md"
      >
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div style={{ gridColumn: 'span 2' }}>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Nombre *
              </label>
              <input
                type="text"
                value={formData.nombre}
                onChange={(e) => setFormData({...formData, nombre: e.target.value})}
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

            <div style={{ gridColumn: 'span 2' }}>
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

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Precio *
              </label>
              <input
                type="number"
                value={formData.precio}
                onChange={(e) => setFormData({...formData, precio: e.target.value})}
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
                Stock *
              </label>
              <input
                type="number"
                value={formData.stock}
                onChange={(e) => setFormData({...formData, stock: e.target.value})}
                required
                min="0"
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
                }}
              />
            </div>

            <div style={{ gridColumn: 'span 2' }}>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Proveedor *
              </label>
              <select
                value={formData.idProveedor}
                onChange={(e) => setFormData({...formData, idProveedor: e.target.value})}
                required
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
                }}
              >
                <option value="">Seleccionar proveedor</option>
                {proveedores.map(proveedor => (
                  <option key={proveedor.id} value={proveedor.id}>
                    {proveedor.nombre}
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
              {editingRepuesto ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default RepuestosPage;