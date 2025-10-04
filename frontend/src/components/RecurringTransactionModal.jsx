import { useState, useEffect } from 'react';
import { authenticatedFetch } from '../services/api';

// O componente recebe 'isOpen' (para saber se está visível) e 'onClose' (função para se fechar)
function RecurringTransactionModal({ isOpen, onClose }) {
  const [recurringTransactions, setRecurringTransactions] = useState([]);

  // Este efeito roda sempre que a prop 'isOpen' muda
  useEffect(() => {
    // Só buscamos os dados se o modal estiver sendo aberto
    if (isOpen) {
      const fetchRecurring = async () => {
        try {
          const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/recurring-transactions`;
          const response = await authenticatedFetch(apiUrl);
          if (response.ok) {
            const data = await response.json();
            setRecurringTransactions(data);
          }
        } catch (error) {
          console.error("Erro ao buscar transações recorrentes:", error);
        }
      };
      fetchRecurring();
    }
  }, [isOpen]);

  const handleDelete = async (id) => {
    if (!window.confirm("Tem certeza que deseja deletar este item recorrente?")) {
      return;
    }
    try {
      const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/recurring-transactions/${id}`;
      const response = await authenticatedFetch(apiUrl, {
        method: 'DELETE',
      });
      if (response.ok) {
        setRecurringTransactions(current => current.filter(item => item.id !== id));
      }
    } catch (error) {
      console.error("Erro ao deletar item recorrente:", error);
    }
  };

  // Se o modal não estiver aberto, não renderiza nada
  if (!isOpen) {
    return null;
  }

  // JSX para a estrutura do modal
  return (
    // Fundo do modal
    <div style={modalOverlayStyle}>
      {/* Conteúdo do modal */}
      <div style={modalContentStyle}>
        <h2>Gerenciar Itens Recorrentes</h2>
        <button onClick={onClose} style={{ position: 'absolute', top: '10px', right: '10px', cursor: 'pointer' }}>X</button>
        
        <hr />
        
        <h4>Itens Cadastrados:</h4>
        <ul style={{ listStyle: 'none', padding: 0, maxHeight: '200px', overflowY: 'auto' }}>
          {recurringTransactions.map(item => (
            <li key={item.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '5px', borderBottom: '1px solid #eee' }}>
              <span>{item.description} - R$ {item.amount} (Todo dia {item.dayOfMonth}) - {item.transactionType}</span>
              <button onClick={() => handleDelete(item.id)}>Deletar</button>
            </li>
          ))}
        </ul>
        
        {/* O formulário para adicionar novos itens virá aqui */}
        
      </div>
    </div>
  );
}

// Estilos básicos para o modal
const modalOverlayStyle = {
  position: 'fixed',
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
  backgroundColor: 'rgba(0, 0, 0, 0.7)',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  zIndex: 1000,
};

const modalContentStyle = {
  background: 'white',
  padding: '20px',
  borderRadius: '8px',
  position: 'relative',
  width: '600px',
  maxWidth: '90%',
  boxShadow: '0 5px 15px rgba(0,0,0,0.3)',
};

export default RecurringTransactionModal;