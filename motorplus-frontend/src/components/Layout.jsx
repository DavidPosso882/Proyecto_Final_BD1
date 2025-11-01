import React from 'react';

const Layout = ({ children, currentPage, setCurrentPage }) => {
  const [isOpen, setIsOpen] = React.useState(false);
  const isMobile = window.innerWidth < 768;

  const menuItems = [
    { id: 'clientes', label: 'Clientes', icon: '👥' },
    { id: 'vehiculos', label: 'Vehículos', icon: '🚗' },
    { id: 'mecanicos', label: 'Mecánicos', icon: '🔧' },
    { id: 'repuestos', label: 'Repuestos', icon: '🔩' },
    { id: 'proveedores', label: 'Proveedores', icon: '🏢' },
    { id: 'ordenes', label: 'Órdenes de Trabajo', icon: '📋' },
    { id: 'servicios', label: 'Servicios', icon: '⚙️' },
    { id: 'facturas', label: 'Facturas', icon: '💰' },
    { id: 'reportes', label: 'Reportes', icon: '📊' }
  ];

  const sidebarBg = '#2563eb';
  const sidebarText = 'white';
  const mainBg = '#f8fafc';
  const headerBg = 'white';

  const SidebarContent = () => (
    <div style={{ padding: '16px' }}>
      <div style={{ textAlign: 'center', marginBottom: '24px' }}>
        <h2 style={{ fontSize: '1.5rem', color: sidebarText, marginBottom: '4px', fontWeight: '600' }}>
          MotorPlus
        </h2>
        <p style={{ fontSize: '0.875rem', color: sidebarText, opacity: 0.8 }}>
          Sistema de Gestión
        </p>
      </div>
      <div>
        {menuItems.map(item => (
          <button
            key={item.id}
            style={{
              width: '100%',
              padding: '12px 16px',
              marginBottom: '4px',
              backgroundColor: currentPage === item.id ? 'rgba(255,255,255,0.2)' : 'transparent',
              color: sidebarText,
              border: 'none',
              borderRadius: '8px',
              textAlign: 'left',
              fontSize: '1rem',
              fontWeight: '500',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              transition: 'all 0.2s ease',
            }}
            onClick={() => {
              setCurrentPage(item.id);
              if (isMobile) setIsOpen(false);
            }}
            onMouseEnter={(e) => {
              if (currentPage !== item.id) {
                e.target.style.backgroundColor = 'rgba(255,255,255,0.1)';
              }
            }}
            onMouseLeave={(e) => {
              if (currentPage !== item.id) {
                e.target.style.backgroundColor = 'transparent';
              }
            }}
          >
            <span style={{ fontSize: '1.2em', marginRight: '12px' }}>{item.icon}</span>
            {item.label}
          </button>
        ))}
      </div>
    </div>
  );

  return (
    <div style={{ minHeight: '100vh', backgroundColor: mainBg, display: 'flex' }}>
      {/* Desktop Sidebar */}
      {!isMobile && (
        <div
          style={{
            width: '280px',
            backgroundColor: sidebarBg,
            color: sidebarText,
            position: 'fixed',
            height: '100vh',
            overflowY: 'auto',
            boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
          }}
        >
          <SidebarContent />
        </div>
      )}

      {/* Mobile Drawer */}
      {isMobile && (
        <div
          style={{
            position: 'fixed',
            top: '0',
            left: '0',
            width: '280px',
            height: '100vh',
            backgroundColor: sidebarBg,
            color: sidebarText,
            transform: isOpen ? 'translateX(0)' : 'translateX(-100%)',
            transition: 'transform 0.3s ease',
            zIndex: '1000',
            boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
          }}
        >
          <div style={{ padding: '16px', borderBottom: '1px solid rgba(255,255,255,0.2)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <h2 style={{ fontSize: '1.5rem', color: sidebarText, marginBottom: '4px', fontWeight: '600' }}>
                  MotorPlus
                </h2>
                <p style={{ fontSize: '0.875rem', color: sidebarText, opacity: 0.8 }}>
                  Sistema de Gestión
                </p>
              </div>
              <button
                style={{
                  background: 'none',
                  border: 'none',
                  color: sidebarText,
                  fontSize: '1.5rem',
                  cursor: 'pointer',
                  padding: '4px',
                  borderRadius: '4px',
                }}
                onClick={() => setIsOpen(false)}
                onMouseEnter={(e) => e.target.style.backgroundColor = 'rgba(255,255,255,0.1)'}
                onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
              >
                ✕
              </button>
            </div>
          </div>
          <div style={{ padding: '16px' }}>
            {menuItems.map(item => (
              <button
                key={item.id}
                style={{
                  width: '100%',
                  padding: '12px 16px',
                  marginBottom: '4px',
                  backgroundColor: currentPage === item.id ? 'rgba(255,255,255,0.2)' : 'transparent',
                  color: sidebarText,
                  border: 'none',
                  borderRadius: '8px',
                  textAlign: 'left',
                  fontSize: '1rem',
                  fontWeight: '500',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  transition: 'all 0.2s ease',
                }}
                onClick={() => {
                  setCurrentPage(item.id);
                  setIsOpen(false);
                }}
                onMouseEnter={(e) => {
                  if (currentPage !== item.id) {
                    e.target.style.backgroundColor = 'rgba(255,255,255,0.1)';
                  }
                }}
                onMouseLeave={(e) => {
                  if (currentPage !== item.id) {
                    e.target.style.backgroundColor = currentPage === item.id ? 'rgba(255,255,255,0.2)' : 'transparent';
                  }
                }}
              >
                <span style={{ fontSize: '1.2em', marginRight: '12px' }}>{item.icon}</span>
                {item.label}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Mobile Overlay */}
      {isMobile && isOpen && (
        <div
          style={{
            position: 'fixed',
            top: '0',
            left: '0',
            width: '100vw',
            height: '100vh',
            backgroundColor: 'rgba(0,0,0,0.5)',
            zIndex: '999',
          }}
          onClick={() => setIsOpen(false)}
        />
      )}

      {/* Main Content */}
      <div style={{ flex: '1', marginLeft: !isMobile ? '280px' : '0' }}>
        {/* Header */}
        <header
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            padding: '1.5rem',
            backgroundColor: headerBg,
            boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
            borderBottom: '1px solid #e5e7eb',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center' }}>
            {isMobile && (
              <button
                style={{
                  background: 'none',
                  border: 'none',
                  fontSize: '1.5rem',
                  marginRight: '16px',
                  cursor: 'pointer',
                  padding: '4px',
                  borderRadius: '4px',
                }}
                onClick={() => setIsOpen(true)}
                aria-label="Open menu"
              >
                ☰
              </button>
            )}
            <h1 style={{ fontSize: '1.875rem', fontWeight: '600', color: '#1f2937' }}>
              {menuItems.find(item => item.id === currentPage)?.label || 'MotorPlus'}
            </h1>
          </div>
        </header>

        {/* Page Content */}
        <div style={{ padding: '24px', minHeight: 'calc(100vh - 80px)' }}>
          {children}
        </div>
      </div>
    </div>
  );
};

export default Layout;