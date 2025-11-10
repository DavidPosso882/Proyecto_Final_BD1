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
    ordenCodigo: '',
    fechaEmision: '',
    subtotal: '',
    total: '',
    estadoPago: 'PENDIENTE'
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
          ordenCodigo: parseInt(formData.ordenCodigo),
          subtotal: parseFloat(formData.subtotal),
          total: parseFloat(formData.total),
          estadoPago: formData.estadoPago,
          fechaEmision: formData.fechaEmision || new Date().toISOString().split('T')[0]
        })
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Error del servidor:', errorText);
        throw new Error('Error al guardar factura: ' + errorText);
      }

      await fetchFacturas();
      setIsModalOpen(false);
      resetForm();
    } catch (err) {
      setError(err.message);
      console.error('Error completo:', err);
    }
  };

  const handleEdit = (factura) => {
    setEditingFactura(factura);
    
    // Manejar la fecha correctamente sin conversión de zona horaria
    let fechaFormateada = '';
    if (factura.fechaEmision) {
      if (typeof factura.fechaEmision === 'string') {
        fechaFormateada = factura.fechaEmision.split('T')[0];
      } else {
        fechaFormateada = factura.fechaEmision;
      }
    }
    
    setFormData({
      ordenCodigo: factura.ordenCodigo ? factura.ordenCodigo.toString() : '',
      fechaEmision: fechaFormateada,
      subtotal: factura.subtotal ? factura.subtotal.toString() : '',
      total: factura.total ? factura.total.toString() : '',
      estadoPago: factura.estadoPago || 'PENDIENTE'
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

  const handleDownloadPDF = async (factura) => {
    try {
      console.log('Descargando PDF para factura:', factura.idFactura);
      const response = await fetch(`http://localhost:8080/api/reports/export/factura/${factura.idFactura}`);
      
      if (!response.ok) {
        const errorText = await response.text();
        console.error('Error del servidor:', errorText);
        throw new Error(`Error al generar el PDF: ${response.status}`);
      }

      // Verificar que es un PDF
      const contentType = response.headers.get('content-type');
      console.log('Content-Type:', contentType);
      
      // Obtener el blob del PDF
      const blob = await response.blob();
      console.log('Blob recibido, tamaño:', blob.size);
      
      // Crear un URL temporal para el blob
      const url = window.URL.createObjectURL(blob);
      
      // Crear un enlace temporal y hacer clic en él para descargar
      const a = document.createElement('a');
      a.href = url;
      a.download = `factura_${factura.idFactura}.pdf`;
      document.body.appendChild(a);
      a.click();
      
      // Limpiar
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      
      console.log('PDF descargado exitosamente');
    } catch (err) {
      console.error('Error al descargar PDF:', err);
      setError(`Error al descargar el PDF de la factura: ${err.message}`);
    }
  };

  const resetForm = () => {
    setFormData({
      ordenCodigo: '',
      fechaEmision: '',
      subtotal: '',
      total: '',
      estadoPago: 'PENDIENTE'
    });
    setEditingFactura(null);
  };

  const openCreateModal = () => {
    resetForm();
    // Establecer la fecha actual en formato local
    const hoy = new Date();
    const fechaLocal = new Date(hoy.getTime() - (hoy.getTimezoneOffset() * 60000))
      .toISOString()
      .split('T')[0];
    setFormData(prev => ({
      ...prev,
      fechaEmision: fechaLocal
    }));
    setIsModalOpen(true);
  };

  // Calcular total automáticamente (subtotal + 19% IVA)
  const calculateTotal = () => {
    const subtotal = parseFloat(formData.subtotal) || 0;
    const iva = subtotal * 0.19;
    return (subtotal + iva).toFixed(2);
  };

  // Actualizar total cuando cambie el subtotal
  useEffect(() => {
    if (formData.subtotal) {
      setFormData(prev => ({
        ...prev,
        total: calculateTotal()
      }));
    }
  }, [formData.subtotal]);

  const columns = [
    { key: 'idFactura', label: 'ID' },
    {
      key: 'ordenCodigo',
      label: 'Orden',
      render: (item) => `Orden ${item.ordenCodigo || 'N/A'}`
    },
    {
      key: 'fechaEmision',
      label: 'Fecha Emisión',
      render: (item) => {
        if (!item.fechaEmision) return 'N/A';
        // Extraer solo la fecha sin conversión de zona horaria
        const fecha = typeof item.fechaEmision === 'string' 
          ? item.fechaEmision.split('T')[0] 
          : item.fechaEmision;
        // Formatear manualmente para evitar problemas de zona horaria
        const [año, mes, dia] = fecha.split('-');
        return `${dia}/${mes}/${año}`;
      }
    },
    {
      key: 'subtotal',
      label: 'Subtotal',
      render: (item) => `$${item.subtotal.toLocaleString()}`
    },
    {
      key: 'total',
      label: 'Total',
      render: (item) => `$${item.total.toLocaleString()}`
    },
    { key: 'estadoPago', label: 'Estado' }
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
        onDownloadPDF={handleDownloadPDF}
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
                value={formData.ordenCodigo}
                onChange={(e) => setFormData({...formData, ordenCodigo: e.target.value})}
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
                  <option key={orden.codigo} value={orden.codigo}>
                    Orden {orden.codigo} - {orden.vehiculo?.marca || 'N/A'} {orden.vehiculo?.modelo || ''}
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
                value={formData.estadoPago}
                onChange={(e) => setFormData({...formData, estadoPago: e.target.value})}
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
              <div style={{ position: 'relative' }}>
                <span style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#6b7280', fontWeight: '500' }}>$</span>
                <input
                  type="number"
                  value={formData.subtotal}
                  onChange={(e) => setFormData({...formData, subtotal: e.target.value})}
                  required
                  min="0"
                  step="0.01"
                  placeholder="0.00"
                  style={{
                    width: '100%',
                    padding: '8px 12px 8px 28px',
                    border: '1px solid #d1d5db',
                    borderRadius: '6px',
                    fontSize: '0.875rem',
                  }}
                />
              </div>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                IVA (19%)
              </label>
              <div style={{ position: 'relative' }}>
                <span style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#6b7280', fontWeight: '500' }}>$</span>
                <input
                  type="text"
                  value={(parseFloat(formData.subtotal) * 0.19 || 0).toFixed(2)}
                  readOnly
                  style={{
                    width: '100%',
                    padding: '8px 12px 8px 28px',
                    border: '1px solid #d1d5db',
                    borderRadius: '6px',
                    fontSize: '0.875rem',
                    backgroundColor: '#f9fafb',
                    color: '#6b7280',
                  }}
                />
              </div>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '4px' }}>
                Total
              </label>
              <div style={{ position: 'relative' }}>
                <span style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#16a34a', fontWeight: '600' }}>$</span>
                <input
                  type="text"
                  value={formData.total}
                  readOnly
                  style={{
                    width: '100%',
                    padding: '8px 12px 8px 28px',
                    border: '2px solid #16a34a',
                    borderRadius: '6px',
                    fontSize: '0.875rem',
                    backgroundColor: '#f0fdf4',
                    fontWeight: '600',
                    color: '#16a34a',
                  }}
                />
              </div>
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