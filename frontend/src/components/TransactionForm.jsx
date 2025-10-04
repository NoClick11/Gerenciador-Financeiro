import { useState } from 'react';
import { authenticatedFetch } from '../services/api';
// Supondo que você tenha o Enum RecurrenceType no backend
// Se não, podemos usar as strings "ONE_TIME" e "RECURRING"

function TransactionForm({ onTransactionAdded }) {
  const [description, setDescription] = useState('');
  const [amount, setAmount] = useState('');
  const [type, setType] = useState('EXPENSE');
  const [recurrenceType, setRecurrenceType] = useState('ONE_TIME'); 

  const handleSubmit = async (event) => {
    event.preventDefault();

    const newTransaction = {
      description,
      amount,
      type,
      recurrenceType,
    };

    try {
      const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/transactions`;
      const response = await authenticatedFetch(apiUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newTransaction),
      });

      if (!response.ok) throw new Error('Erro ao criar a transação');
      
      const createdTransaction = await response.json();
      onTransactionAdded(createdTransaction);
      
      setDescription('');
      setAmount('');
    } catch (error) {
      console.error("Houve um erro ao enviar o formulário:", error);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ border: '1px solid #ccc', padding: '20px', borderRadius: '8px' }}>
      <h3>Adicionar Nova Transação</h3>
      <div>
          <label>Descrição: </label>
          <input type="text" value={description} onChange={e => setDescription(e.target.value)} required />
      </div>
      <div>
          <label>Valor (R$): </label>
          <input type="number" step="0.01" value={amount} onChange={e => setAmount(e.target.value)} required />
      </div>
      <div>
        <label>Tipo: </label>
        <select value={type} onChange={e => setType(e.target.value)}>
          <option value="EXPENSE">Saída</option>
          <option value="INCOME">Entrada</option>
        </select>
      </div>
      <div>
        <label>Recorrência:</label>
        <select value={recurrenceType} onChange={e => setRecurrenceType(e.target.value)}>
          <option value="ONE_TIME">Única</option>
          <option value="RECURRING">Recorrente</option>
        </select>
      </div>
      <button type="submit" style={{ marginTop: '10px' }}>Adicionar Transação</button>
    </form>
  );
}

export default TransactionForm;