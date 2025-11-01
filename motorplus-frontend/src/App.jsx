import React, { useState } from 'react';
import Layout from './components/Layout.jsx';
import ClientesPage from './pages/ClientesPage.jsx';
import VehiculosPage from './pages/VehiculosPage.jsx';
import MecanicosPage from './pages/MecanicosPage.jsx';
import RepuestosPage from './pages/RepuestosPage.jsx';
import ProveedoresPage from './pages/ProveedoresPage.jsx';
import OrdenesPage from './pages/OrdenesPage.jsx';
import ServiciosPage from './pages/ServiciosPage.jsx';
import FacturasPage from './pages/FacturasPage.jsx';
import ReportesPage from './pages/ReportesPage.jsx';
import './styles/App.css';

// Simple inline styles for better appearance
const appStyles = {
  fontFamily: 'Inter, system-ui, -apple-system, sans-serif',
  backgroundColor: '#f8fafc',
  minHeight: '100vh',
  color: '#1e293b'
};

const containerStyles = {
  maxWidth: '1200px',
  margin: '0 auto',
  padding: '20px'
};

function App() {
  const [currentPage, setCurrentPage] = useState('clientes');

  const renderPage = () => {
    switch (currentPage) {
      case 'clientes':
        return <ClientesPage />;
      case 'vehiculos':
        return <VehiculosPage />;
      case 'mecanicos':
        return <MecanicosPage />;
      case 'repuestos':
        return <RepuestosPage />;
      case 'proveedores':
        return <ProveedoresPage />;
      case 'ordenes':
        return <OrdenesPage />;
      case 'servicios':
        return <ServiciosPage />;
      case 'facturas':
        return <FacturasPage />;
      case 'reportes':
        return <ReportesPage />;
      default:
        return <ClientesPage />;
    }
  };

  return (
    <div style={appStyles}>
      <Layout currentPage={currentPage} setCurrentPage={setCurrentPage}>
        <div style={containerStyles}>
          {renderPage()}
        </div>
      </Layout>
    </div>
  );
}

export default App;