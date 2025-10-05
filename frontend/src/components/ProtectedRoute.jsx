import { Navigate, Outlet, useOutletContext } from 'react-router-dom';

function ProtectedRoute() {
  const context = useOutletContext();
  const token = localStorage.getItem('jwt-token');

  if (!token) {
    return <Navigate to="/login" />;
  }

  return <Outlet context={context} />;
}

export default ProtectedRoute;