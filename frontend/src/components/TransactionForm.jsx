import { useState } from 'react';
import { authenticatedFetch } from '../services/api';

function TransactionForm({ onTransactionAdded }) {
  const [description, setDescription] = useState('');
  const [amount, setAmount] = useState('');
  const [type, setType] = useState('EXPENSE');
  const [expenseCategory, setExpenseCategory] = useState('SPORADIC');

  const handleSubmit = async (event) => {
    event.preventDefault();

    const newTransaction = {
      description,
      amount,
      type,
    };
    
    if (type === 'EXPENSE') {
      newTransaction.expenseCategory = expenseCategory;
    }

    try {
      const response = await authenticatedFetch('http://localhost:8080/api/transactions', {
        method: 'POST',
        body: JSON.stringify(newTransaction),
      });

      if (!response.ok) throw new Error('Erro ao criar a transação');
      
      const createdTransaction = await response.json();
      onTransactionAdded(createdTransaction);
      
      setDescription('');
      setAmount('');
      setType('EXPENSE');
      setExpenseCategory('SPORADIC');

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

      {}
      {type === 'EXPENSE' && (
        <div>
          <label>Categoria da Despesa:</label>
          <div>
            <input 
              type="radio" 
              id="sporadic" 
              name="expenseCategory" 
              value="SPORADIC" 
              checked={expenseCategory === 'SPORADIC'}
              onChange={e => setExpenseCategory(e.target.value)}
            />
            <label htmlFor="sporadic">Esporádico</label>
          </div>
          <div>
            <input 
              type="radio" 
              id="monthly" 
              name="expenseCategory" 
              value="MONTHLY" 
              checked={expenseCategory === 'MONTHLY'}
              onChange={e => setExpenseCategory(e.target.value)}
            />
            <label htmlFor="monthly">Mensal</label>
          </div>
        </div>
      )}
      
      <button type="submit" style={{ marginTop: '10px' }}>Adicionar Transação</button>
    </form>
  );
}

export default TransactionForm;