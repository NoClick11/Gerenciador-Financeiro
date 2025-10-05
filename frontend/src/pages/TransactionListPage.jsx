import { useOutletContext } from 'react-router-dom';
import TransactionForm from '../components/TransactionForm';
import TransactionChart from '../components/TransactionChart';

function TransactionListPage() {
  // Pega todos os dados e funções do "pai" (App.jsx) via contexto
  const {
    transactions,
    handleTransactionAdded,
    handleDelete,
    totalIncome,
    totalExpense,
    currentDate,
    handlePreviousMonth,
    handleNextMonth,
    aiSuggestion,
    isLoadingAI,
    handleGenerateSuggestion,
    setAiSuggestion // Assumindo que você passou esta função no context do App.jsx
  } = useOutletContext();
  
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth() + 1;

  return (
    <div>
      {/* O Gráfico foi movido para o topo para dar mais destaque */}
      <div style={{ display: 'flex', justifyContent: 'center', gap: '50px', alignItems: 'center', flexWrap: 'wrap', marginBottom: '20px', padding: '20px', background: '#f9f9f9', borderRadius: '8px' }}>
        <TransactionChart income={totalIncome} expense={totalExpense} />
      </div>
      
      <TransactionForm onTransactionAdded={handleTransactionAdded} />
      <hr style={{ margin: '30px 0' }}/>
      
      <div style={{ marginBottom: '30px' }}>
        <h2>Análise com IA</h2>
        <button onClick={handleGenerateSuggestion} disabled={isLoadingAI}>
          {isLoadingAI ? 'Analisando...' : 'Gerar Nova Análise'}
        </button>
        {isLoadingAI && <p style={{ color: '#007bff' }}>Aguarde...</p>}
        {aiSuggestion && !isLoadingAI && (
          <div style={{ marginTop: '15px', padding: '15px', border: '1px solid #007bff', borderRadius: '5px', background: '#f0f8ff' }}>
            <h3>Sugestão do Assistente {aiSuggestion.createdAt && `(de ${new Date(aiSuggestion.createdAt).toLocaleString()})`}:</h3>
            <pre style={{ whiteSpace: 'pre-wrap', fontFamily: 'inherit', fontSize: '1em' }}>{aiSuggestion.suggestionText}</pre>
            {/* O botão de limpar precisa da função setAiSuggestion vinda do App.jsx */}
            <button onClick={() => setAiSuggestion(null)} style={{ marginTop: '10px' }}>Limpar</button>
          </div>
        )}
      </div>

      <hr style={{ margin: '30px 0' }}/>
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Histórico de Transações</h2>
        <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
          <button onClick={handlePreviousMonth}>&lt; Mês Anterior</button>
          <span style={{ fontWeight: 'bold', minWidth: '70px', textAlign: 'center' }}>
            {String(month).padStart(2, '0')}/{year}
          </span>
          <button onClick={handleNextMonth}>Próximo Mês &gt;</button>
        </div>
      </div>
      
      <ul className="transaction-list">
        {transactions.map(transaction => (
          <li 
            key={transaction.id} 
            className={`transaction-item ${transaction.type === 'INCOME' ? 'transaction-item--income' : 'transaction-item--expense'}`}
          >
            <div className="transaction-item-details">
              <span>{transaction.date}</span>
              <span>{transaction.description}</span>
              <strong>
                {transaction.type === 'INCOME' ? '+' : '-'} R$ {parseFloat(transaction.amount).toFixed(2)}
              </strong>
            </div>
            <button onClick={() => handleDelete(transaction)}>
              X
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TransactionListPage;