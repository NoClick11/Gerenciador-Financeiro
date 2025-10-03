import { Navigate, Outlet } from 'react-router-dom';

function ProtectedRoute() {
  const token = localStorage.getItem('jwt-token');

  if (!token) {
    console.log("Acesso negado. Redirecionando para /login.");
    return <Navigate to="/login" />;
  }

  return <Outlet />;
}

export default ProtectedRoute;