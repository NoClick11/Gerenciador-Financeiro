import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

function RegisterPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/auth/register`;
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage('');
    setIsLoading(true);

    try {
      const response = await fetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || 'Falha no registro');
      }

      console.log('Registro bem-sucedido!');
      setMessage('Usuário registrado com sucesso! Redirecionando para o login...');

      setTimeout(() => {
        navigate('/login');
      }, 2000);


    } catch (err) {
      console.error("Erro no registro:", err);
      setMessage(`Erro: ${err.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <h2>Criar Conta</h2>

      {}
      {message && (
        <div style={{
          padding: '10px',
          margin: '15px 0',
          border: message.startsWith('Erro:') ? '1px solid #dc3545' : '1px solid #198754',
          backgroundColor: message.startsWith('Erro:') ? '#f8d7da' : '#d4edda',
          color: message.startsWith('Erro:') ? '#721c24' : '#155724',
          borderRadius: '4px',
          textAlign: 'center'
        }}>
          {message}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '10px' }}>
          <label htmlFor="username">Nome de usuário:</label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
            disabled={isLoading}
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="password">Senha:</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
            disabled={isLoading}
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>

        {}

        <button type="submit" disabled={isLoading} style={{ width: '100%', padding: '10px' }}>
          {isLoading ? 'Registrando...' : 'Registrar'}
        </button>
      </form>
      <p style={{ marginTop: '15px', textAlign: 'center' }}>
        Já tem uma conta? <Link to="/login">Faça o login aqui</Link>.
      </p>
    </div>
  );
}

export default RegisterPage;