
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/auth/login`;
  

  const navigate = useNavigate();

  const handleSubmit = async (event) => {

    event.preventDefault();
    setError('');

    try {

      
      const response = await fetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        throw new Error('Credenciais inválidas');
      }

      const data = await response.json();
      const token = data.token;

      localStorage.setItem('jwt-token', token);
      console.log('Login bem-sucedido, token salvo!');

      navigate('/');

    } catch (err) {
      console.error("Erro no login:", err);
      setError('Usuário ou senha inválidos. Tente novamente.');
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: 'auto', padding: '20px' }}>
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '10px' }}>
          <label>Nome de usuário:</label>
          <input
            type="text"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
            style={{ width: '100%', padding: '8px' }}
          />
        </div>
        <div style={{ marginBottom: '10px' }}>
          <label>Senha:</label>
          <input
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
            style={{ width: '100%', padding: '8px' }}
          />
        </div>
        
        {}
        {error && <p style={{ color: 'red' }}>{error}</p>}
        
        <button type="submit" style={{ width: '100%', padding: '10px' }}>Entrar</button>
      </form>
      <p style={{ marginTop: '15px' }}>
        Não tem uma conta? <Link to="/register">Crie uma aqui</Link>.
      </p>
    </div>
  );
}

export default LoginPage;