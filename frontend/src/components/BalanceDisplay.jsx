
function BalanceDisplay({ income, expense, balance }) {
  const getBalanceColor = () => {
    if (balance > 0) return 'green';
    if (balance < 0) return 'red';
    return 'black';
  };
  
  return (
    <div style={{ marginBottom: '20px' }}>
      <h2>Balanço Atual</h2>
      <div style={{ display: 'flex', justifyContent: 'space-around', padding: '10px', border: '1px solid #ccc', borderRadius: '5px' }}>
        <div style={{ color: 'green' }}>
          <h4>Entradas</h4>
          <p>R$ {income.toFixed(2)}</p>
        </div>
        <div style={{ color: 'red' }}>
          <h4>Saídas</h4>
          <p>R$ {expense.toFixed(2)}</p>
        </div>
        <div>
          <h4>Saldo</h4>
          <p style={{ color: getBalanceColor(), fontWeight: 'bold' }}>
            R$ {balance.toFixed(2)}
          </p>
        </div>
      </div>
    </div>
  );
}

export default BalanceDisplay;