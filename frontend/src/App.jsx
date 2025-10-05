import { Outlet, Link, useNavigate } from 'react-router-dom';
import './App.css';

function App() {
  const navigate = useNavigate();
  
  const token = localStorage.getItem('jwt-token');

  const handleLogout = () => {
    localStorage.removeItem('jwt-token');
    console.log("Usuário deslogado, token removido.");
    
    navigate('/login');
    
    window.location.reload();
  };

  return (
    <div className="main-container">
      {}
      <nav className="navbar">
        <div>
          {}
          {token && <Link to="/">Dashboard</Link>}
        </div>
        <div>
          {}
          {token ? (
            <button onClick={handleLogout}>Sair</button>
          ) : (
            <>
              <Link to="/login" style={{ marginRight: '10px' }}>Login</Link>
              <Link to="/register">Registro</Link>
            </>
          )}
        </div>
      </nav>

      <main>
        {}
        <Outlet />
      </main>
    </div>
  );
}

export default App;