import { useState, useEffect } from 'react';
import { authenticatedFetch } from '../services/api';


function RecurringTransactionModal({ isOpen, onClose }) {
  const [recurringTransactions, setRecurringTransactions] = useState([]);

 
  useEffect(() => {

    if (isOpen) {
      const fetchRecurring = async () => {
        try {
          const response = await authenticatedFetch('/api/recurring-transactions');
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
      const response = await authenticatedFetch(`/api/recurring-transactions/${id}`, {
        method: 'DELETE',
      });
      if (response.ok) {
        setRecurringTransactions(current => current.filter(item => item.id !== id));
      }
    } catch (error) {
      console.error("Erro ao deletar item recorrente:", error);
    }
  };

  if (!isOpen) {
    return null;
  }

  return (
    <div style={modalOverlayStyle}>
      {}
      <div style={modalContentStyle}>
        <h2>Gerenciar Itens Recorrentes</h2>
        <button onClick={onClose} style={{ position: 'absolute', top: '10px', right: '10px' }}>X</button>
        
        <hr />
        
        {}
        <h4>Itens Cadastrados:</h4>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {recurringTransactions.map(item => (
            <li key={item.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '5px', borderBottom: '1px solid #eee' }}>
              <span>{item.description} - R$ {item.amount} (Todo dia {item.dayOfMonth}) - {item.transactionType}</span>
              <button onClick={() => handleDelete(item.id)}>Deletar</button>
            </li>
          ))}
        </ul>

        {}
      </div>
    </div>
  );
}

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
};

const modalContentStyle = {
  background: 'white',
  padding: '20px',
  borderRadius: '8px',
  position: 'relative',
  width: '500px',
  maxWidth: '90%',
};

export default RecurringTransactionModal;