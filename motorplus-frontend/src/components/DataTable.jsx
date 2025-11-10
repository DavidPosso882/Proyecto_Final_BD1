import React from 'react';

const DataTable = ({ data, columns, title, onEdit, onDelete, onView }) => {
  const tableBg = 'white';
  const borderColor = 'gray.200';
  const hoverBg = 'gray.50';

  return (
    <div
      style={{
        backgroundColor: 'white',
        borderRadius: '12px',
        boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
        overflow: 'hidden',
        border: '1px solid #e5e7eb',
      }}
    >
      <div style={{ padding: '24px', borderBottom: '1px solid #e5e7eb' }}>
        <h3 style={{ fontSize: '1.25rem', fontWeight: 'bold', color: '#1f2937' }}>
          {title}
        </h3>
      </div>

      <div style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead style={{ backgroundColor: '#f9fafb' }}>
            <tr>
              {columns.map(column => (
                <th
                  key={column.key}
                  style={{
                    padding: '12px 16px',
                    textAlign: 'left',
                    fontWeight: 'bold',
                    color: '#374151',
                    borderBottom: '1px solid #e5e7eb',
                    fontSize: '0.875rem',
                    textTransform: 'uppercase',
                    letterSpacing: '0.05em',
                  }}
                >
                  {column.label}
                </th>
              ))}
              <th
                style={{
                  padding: '12px 16px',
                  textAlign: 'left',
                  fontWeight: 'bold',
                  color: '#374151',
                  borderBottom: '1px solid #e5e7eb',
                  fontSize: '0.875rem',
                  textTransform: 'uppercase',
                  letterSpacing: '0.05em',
                }}
              >
                Acciones
              </th>
            </tr>
          </thead>
          <tbody>
            {data.length === 0 ? (
              <tr>
                <td
                  colSpan={columns.length + 1}
                  style={{
                    textAlign: 'center',
                    padding: '48px 16px',
                  }}
                >
                  <div>
                    <p style={{ fontSize: '1.125rem', color: '#6b7280', marginBottom: '8px' }}>
                      No hay datos disponibles
                    </p>
                    <p style={{ fontSize: '0.875rem', color: '#9ca3af' }}>
                      Los registros aparecerán aquí cuando estén disponibles
                    </p>
                  </div>
                </td>
              </tr>
            ) : (
              data.map((item, index) => (
                <tr
                  key={index}
                  style={{
                    transition: 'background-color 0.2s',
                  }}
                  onMouseEnter={(e) => e.target.closest('tr').style.backgroundColor = '#f9fafb'}
                  onMouseLeave={(e) => e.target.closest('tr').style.backgroundColor = 'transparent'}
                >
                  {columns.map(column => (
                    <td
                      key={column.key}
                      style={{
                        padding: '16px',
                        borderBottom: '1px solid #e5e7eb',
                        color: '#374151',
                      }}
                    >
                      {column.render ? column.render(item) : item[column.key]}
                    </td>
                  ))}
                  <td
                    style={{
                      padding: '16px',
                      borderBottom: '1px solid #e5e7eb',
                    }}
                  >
                    <div style={{ display: 'flex', gap: '8px' }}>
                      {onView && (
                        <button
                          onClick={() => onView(item)}
                          style={{
                            padding: '6px 12px',
                            backgroundColor: 'transparent',
                            color: '#16a34a',
                            border: '1px solid #16a34a',
                            borderRadius: '6px',
                            fontSize: '0.875rem',
                            cursor: 'pointer',
                            transition: 'all 0.2s',
                          }}
                          onMouseEnter={(e) => {
                            e.target.style.backgroundColor = '#16a34a';
                            e.target.style.color = 'white';
                          }}
                          onMouseLeave={(e) => {
                            e.target.style.backgroundColor = 'transparent';
                            e.target.style.color = '#16a34a';
                          }}
                        >
                          👁️
                        </button>
                      )}
                      <button
                        onClick={() => onEdit && onEdit(item)}
                        style={{
                          padding: '6px 12px',
                          backgroundColor: 'transparent',
                          color: '#2563eb',
                          border: '1px solid #2563eb',
                          borderRadius: '6px',
                          fontSize: '0.875rem',
                          cursor: 'pointer',
                          transition: 'all 0.2s',
                        }}
                        onMouseEnter={(e) => {
                          e.target.style.backgroundColor = '#2563eb';
                          e.target.style.color = 'white';
                        }}
                        onMouseLeave={(e) => {
                          e.target.style.backgroundColor = 'transparent';
                          e.target.style.color = '#2563eb';
                        }}
                      >
                        ✏️
                      </button>
                      <button
                        onClick={() => onDelete && onDelete(item.idCliente || item.placa || item.id || item.idOrdenTrabajo || item.idFactura || item.idServicio || item.idProveedor || item.idMecanico || item.idVehiculo || item.codigo)}
                        style={{
                          padding: '6px 12px',
                          backgroundColor: 'transparent',
                          color: '#dc2626',
                          border: '1px solid #dc2626',
                          borderRadius: '6px',
                          fontSize: '0.875rem',
                          cursor: 'pointer',
                          transition: 'all 0.2s',
                        }}
                        onMouseEnter={(e) => {
                          e.target.style.backgroundColor = '#dc2626';
                          e.target.style.color = 'white';
                        }}
                        onMouseLeave={(e) => {
                          e.target.style.backgroundColor = 'transparent';
                          e.target.style.color = '#dc2626';
                        }}
                      >
                        🗑️
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div
        style={{
          padding: '16px',
          borderTop: '1px solid #e5e7eb',
          backgroundColor: '#f9fafb',
        }}
      >
        <span
          style={{
            backgroundColor: '#dbeafe',
            color: '#1e40af',
            padding: '4px 12px',
            borderRadius: '9999px',
            fontSize: '0.875rem',
            fontWeight: '500',
          }}
        >
          Total de registros: {data.length}
        </span>
      </div>
    </div>
  );
};

export default DataTable;