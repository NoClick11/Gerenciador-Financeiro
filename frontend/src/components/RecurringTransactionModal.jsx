import { useState, useEffect } from 'react';
import { authenticatedFetch } from '../services/api';

function RecurringTransactionModal({ isOpen, onClose }) {
  const [recurringTransactions, setRecurringTransactions] = useState([]);

  // Estados para o novo formulário
  const [description, setDescription] = useState('');
  const [amount, setAmount] = useState('');
  const [dayOfMonth, setDayOfMonth] = useState(1);
  const [transactionType, setTransactionType] = useState('EXPENSE');

  useEffect(() => {
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
    if (!window.confirm("Tem certeza que deseja deletar este item recorrente?")) return;
    try {
      const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/recurring-transactions/${id}`;
      const response = await authenticatedFetch(apiUrl, { method: 'DELETE' });
      if (response.ok) {
        setRecurringTransactions(current => current.filter(item => item.id !== id));
      }
    } catch (error) {
      console.error("Erro ao deletar item recorrente:", error);
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const newRecurring = { description, amount, dayOfMonth: parseInt(dayOfMonth), transactionType };
    try {
      const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/recurring-transactions`;
      const response = await authenticatedFetch(apiUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newRecurring),
      });
      if (!response.ok) throw new Error("Falha ao criar item recorrente");

      const createdItem = await response.json();
      setRecurringTransactions(current => [...current, createdItem]);

      setDescription('');
      setAmount('');
      setDayOfMonth(1);
      setTransactionType('EXPENSE');
    } catch (error) {
      console.error("Erro ao criar item recorrente:", error);
    }
  };

  if (!isOpen) {
    return null;
  }

  return (
    <div style={modalOverlayStyle}>
      <div style={modalContentStyle}>
        <h2>Gerenciar Itens Recorrentes</h2>
        <button onClick={onClose} style={{ position: 'absolute', top: '10px', right: '10px', cursor: 'pointer', background: 'none', border: 'none', fontSize: '1.5rem' }}>&times;</button>
        <hr />
        
        <form onSubmit={handleSubmit} style={{ marginBottom: '20px', display: 'flex', flexWrap: 'wrap', gap: '10px', alignItems: 'flex-end' }}>
          <div style={{flex: '2 1 150px'}}>
            <label>Descrição:</label>
            <input type="text" placeholder="ex: Salário, Aluguel" value={description} onChange={e => setDescription(e.target.value)} required style={{width: '100%'}}/>
          </div>
          <div style={{flex: '1 1 80px'}}>
            <label>Valor:</label>
            <input type="number" placeholder="1500.00" value={amount} onChange={e => setAmount(e.target.value)} required step="0.01" style={{width: '100%'}}/>
          </div>
          <div style={{flex: '1 1 60px'}}>
            <label>Dia do Mês:</label>
            <input type="number" value={dayOfMonth} onChange={e => setDayOfMonth(e.target.value)} required min="1" max="31" style={{width: '100%'}}/>
          </div>
          <div style={{flex: '1 1 100px'}}>
            <label>Tipo:</label>
            <select value={transactionType} onChange={e => setTransactionType(e.target.value)} style={{width: '100%'}}>
              <option value="EXPENSE">Saída</option>
              <option value="INCOME">Entrada</option>
            </select>
          </div>
          <button type="submit">Adicionar</button>
        </form>
        
        <hr />
        
        <h4>Itens Cadastrados:</h4>
        <ul style={{ listStyle: 'none', padding: 0, maxHeight: '200px', overflowY: 'auto' }}>
          {recurringTransactions.map(item => (
            <li key={item.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 5px', borderBottom: '1px solid #eee' }}>
              <span>{item.description} - R$ {item.amount} (Todo dia {item.dayOfMonth}) - {item.transactionType}</span>
              <button onClick={() => handleDelete(item.id)} style={{background: 'darkred', color: 'white', border: 'none', borderRadius: '4px'}}>Deletar</button>
            </li>
          ))}
        </ul>
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
  zIndex: 1000,
};

const modalContentStyle = {
  background: 'white',
  padding: '20px',
  borderRadius: '8px',
  position: 'relative',
  width: '800px',
  maxWidth: '90%',
  boxShadow: '0 5px 15px rgba(0,0,0,0.3)',
};

export default RecurringTransactionModal;