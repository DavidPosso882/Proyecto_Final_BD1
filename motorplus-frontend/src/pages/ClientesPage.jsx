import React, { useState, useEffect } from 'react';
import DataTable from '../components/DataTable.jsx';
import Modal from '../components/Modal.jsx';

const ClientesPage = () => {
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCliente, setEditingCliente] = useState(null);
  const [formData, setFormData] = useState({
    idCliente: '',
    nombre: '',
    apellido: '',
    telefono: '',
    email: ''
  });

  useEffect(() => {
    fetchClientes();
  }, []);

  const fetchClientes = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await fetch('http://localhost:8080/api/clientes');
      if (!response.ok) {
        throw new Error(`Error al cargar clientes: ${response.status} ${response.statusText}`);
      }
      const data = await response.json();
      setClientes(data);
    } catch (err) {
      setError(err.message);
      console.error('Error fetching clientes:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const url = editingCliente
        ? `http://localhost:8080/api/clientes/${editingCliente.idCliente}`
        : 'http://localhost:8080/api/clientes';

      const method = editingCliente ? 'PUT' : 'POST';

      // Prepare data with required fields
      const submitData = {
        idCliente: editingCliente ? editingCliente.idCliente : (formData.idCliente || generateId()),
        nombre: formData.nombre,
        apellido: formData.apellido,
        telefono: formData.telefono,
        email: formData.email,
        tipo: 'INDIVIDUAL' // Default type for new clients
      };

      console.log('Submitting cliente data:', submitData);

      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(submitData)
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.error || `Error al guardar cliente: ${response.status} ${response.statusText}`);
      }

      const savedCliente = await response.json();
      console.log('Cliente guardado:', savedCliente);

      await fetchClientes();
      setIsModalOpen(false);
      resetForm();
    } catch (err) {
      setError(err.message);
      console.error('Error saving cliente:', err);
    }
  };

  const generateId = () => {
    // Generate a simple ID for new clients (you can improve this logic)
    const timestamp = Date.now();
    const random = Math.floor(Math.random() * 1000);
    return `CLI${timestamp}${random}`;
  };

  const handleEdit = (cliente) => {
     setEditingCliente(cliente);
     setFormData({
       idCliente: cliente.idCliente || '',
       nombre: cliente.nombre || '',
       apellido: cliente.apellido || '',
       telefono: cliente.telefono || '',
       email: cliente.email || ''
     });
     setIsModalOpen(true);
   };

  const handleDelete = async (id) => {
    if (!confirm('¿Estás seguro de eliminar este cliente?')) return;

    try {
      const response = await fetch(`http://localhost:8080/api/clientes/${id}`, {
        method: 'DELETE'
      });

      if (!response.ok) throw new Error('Error al eliminar cliente');

      await fetchClientes();
    } catch (err) {
      setError(err.message);
    }
  };

  const resetForm = () => {
    setFormData({
      idCliente: '',
      nombre: '',
      apellido: '',
      telefono: '',
      email: ''
    });
    setEditingCliente(null);
  };

  const openCreateModal = () => {
    resetForm();
    setIsModalOpen(true);
  };

  const columns = [
    { key: 'idCliente', label: 'ID' },
    { key: 'nombre', label: 'Nombre' },
    { key: 'apellido', label: 'Apellido' },
    { key: 'telefono', label: 'Teléfono' },
    { key: 'email', label: 'Email' }
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
          Cargando clientes...
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
            Gestión de Clientes
          </h2>
          <p style={{ color: '#6b7280', marginTop: '4px' }}>
            Administra la información de tus clientes
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
          ➕ Nuevo Cliente
        </button>
      </div>

      <DataTable
        data={clientes}
        columns={columns}
        title="Lista de Clientes"
        onEdit={handleEdit}
        onDelete={handleDelete}
      />

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingCliente ? 'Editar Cliente' : 'Nuevo Cliente'}
        size="md"
      >
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Documento de Identidad *
              </label>
              <input
                type="text"
                value={formData.idCliente}
                onChange={(e) => setFormData({...formData, idCliente: e.target.value})}
                required
                disabled={editingCliente ? true : false}
                placeholder={editingCliente ? '' : 'Ej: 12345678'}
                pattern="[A-Za-z0-9]+"
                title="Solo letras y números permitidos"
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
                  backgroundColor: editingCliente ? '#f9fafb' : 'white',
                }}
              />
            </div>

            <div>
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

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Apellido *
              </label>
              <input
                type="text"
                value={formData.apellido}
                onChange={(e) => setFormData({...formData, apellido: e.target.value})}
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
                Teléfono *
              </label>
              <input
                type="tel"
                value={formData.telefono}
                onChange={(e) => setFormData({...formData, telefono: e.target.value})}
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
                Email
              </label>
              <input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({...formData, email: e.target.value})}
                placeholder="cliente@email.com"
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '0.875rem',
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
              {editingCliente ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default ClientesPage;