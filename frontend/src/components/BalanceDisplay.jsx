import { FaArrowCircleUp, FaArrowCircleDown, FaDollarSign } from 'react-icons/fa';

function BalanceDisplay({ income, expense, balance }) {
  const getBalanceColor = () => {
    if (balance > 0) return 'lightgreen';
    if (balance < 0) return '#ff7675'; // Um vermelho/salmão mais suave
    return 'white';
  };
  
  return (
    <div style={{ display: 'flex', gap: '30px', alignItems: 'center', color: 'white', fontFamily: 'sans-serif' }}>
      
      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
        <FaArrowCircleUp color="lightgreen" size="1.5em" />
        <div>
          <span style={{ fontSize: '0.8em', opacity: 0.8 }}>Entradas</span>
          <div style={{ fontWeight: 'bold', fontSize: '1.1em' }}>R$ {income.toFixed(2)}</div>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
        <FaArrowCircleDown color="#ff7675" size="1.5em" />
        <div>
          <span style={{ fontSize: '0.8em', opacity: 0.8 }}>Saídas</span>
          <div style={{ fontWeight: 'bold', fontSize: '1.1em' }}>R$ {expense.toFixed(2)}</div>
        </div>
      </div>
      
      <div style={{ display: 'flex', alignItems: 'center', gap: '8px', borderLeft: '2px solid rgba(255,255,255,0.5)', paddingLeft: '30px' }}>
        <FaDollarSign color={getBalanceColor()} size="1.5em" />
        <div>
          <span style={{ fontSize: '0.8em', opacity: 0.8 }}>Saldo</span>
          <div style={{ fontWeight: 'bold', fontSize: '1.2em', color: getBalanceColor() }}>
            R$ {balance.toFixed(2)}
          </div>
        </div>
      </div>

    </div>
  );
}

export default BalanceDisplay;