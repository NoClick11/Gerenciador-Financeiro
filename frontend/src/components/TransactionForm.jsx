import { useState } from 'react';

function TransactionForm() {
  const [description, setDescription] = useState('');
  const [amount, setAmount] = useState('');
  const [type, setType] = useState('EXPENSE'); 

  const handleSubmit = async (event) => {
    event.preventDefault();

    const newTransaction = {
      description: description,
      amount: amount,
      type: type,
    };

    try {
      const response = await fetch('http://localhost:8080/api/transactions', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newTransaction),
      });

      if (!response.ok) {
        throw new Error('Erro ao criar a transação');
      }

      console.log('Transação criada com sucesso!');
      setDescription('');
      setAmount('');
      setType('EXPENSE');
      

      window.location.reload();

    } catch (error) {
      console.error("Houve um erro ao enviar o formulário:", error);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h3>Adicionar Nova Transação</h3>
      <div>
        <label>Descrição: </label>
        <input
          type="text"
          value={description}
          onChange={e => setDescription(e.target.value)}
          required
        />
      </div>
      <div>
        <label>Valor (R$): </label>
        <input
          type="number"
          step="0.01"
          value={amount}
          onChange={e => setAmount(e.target.value)}
          required
        />
      </div>
      <div>
        <label>Tipo: </label>
        <select value={type} onChange={e => setType(e.target.value)}>
          <option value="EXPENSE">Saída</option>
          <option value="INCOME">Entrada</option>
        </select>
      </div>
      <button type="submit">Adicionar Transação</button>
    </form>
  );
}

export default TransactionForm;