import './App.css';
import { useMemo, useState } from 'react';

function App() {
  const apiBase = useMemo(() => 'http://localhost:8080', []);

  const [status, setStatus] = useState('');

  const [register, setRegister] = useState({
    username: '',
    password: '',
    sex: 'M',
    height: 180,
    weight: 170,
    activityLevel: 'MODERATELY_ACTIVE',
    goal: 'MAINTAIN',
    goalWeightChangePerWeek: 0,
  });

  const [login, setLogin] = useState({
    username: '',
    password: '',
  });

  const [token, setToken] = useState('');

  async function ping() {
    setStatus('Pinging backend...');
    try {
      const res = await fetch(`${apiBase}/api/health`);
      const text = await res.text();
      setStatus(`Health: ${res.status} ${text}`);
    } catch (e) {
      setStatus(`Health error: ${String(e)}`);
    }
  }

  async function registerUser(e) {
    e.preventDefault();
    setStatus('Registering...');
    try {
      const res = await fetch(`${apiBase}/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(register),
      });
      const json = await res.json();
      if (res.ok && json.token) {
        setToken(json.token);
      }
      setStatus(
        `Register: ${res.status} ${json.message} (userId=${json.userId ?? 'n/a'})` +
          (json.token ? ' — JWT saved' : '')
      );
    } catch (err) {
      setStatus(`Register error: ${String(err)}`);
    }
  }

  async function loginUser(e) {
    e.preventDefault();
    setStatus('Logging in...');
    try {
      const res = await fetch(`${apiBase}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(login),
      });
      const json = await res.json();
      if (res.ok && json.token) {
        setToken(json.token);
      }
      setStatus(
        `Login: ${res.status} ${json.message} (userId=${json.userId ?? 'n/a'})` +
          (json.token ? ' — JWT saved' : '')
      );
    } catch (err) {
      setStatus(`Login error: ${String(err)}`);
    }
  }

  async function fetchMe() {
    if (!token) {
      setStatus('No JWT yet — register or login first.');
      return;
    }
    setStatus('Calling /api/user/me ...');
    try {
      const res = await fetch(`${apiBase}/api/user/me`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const text = await res.text();
      setStatus(`Me: ${res.status} ${text}`);
    } catch (err) {
      setStatus(`Me error: ${String(err)}`);
    }
  }

  return (
    <div className="App">
      <header className="App-header">
        <div style={{ maxWidth: 720, width: '100%', textAlign: 'left' }}>
          <h2 style={{ marginTop: 0 }}>Fitness Tracker API Test Page</h2>
          <p style={{ opacity: 0.9 }}>
            This is a temporary landing page to verify the backend endpoints work. Password must be at least 8
            characters (server validation).
          </p>

          <button onClick={ping} style={{ marginBottom: 12 }}>
            Ping backend (/api/health)
          </button>
          <button onClick={fetchMe} style={{ marginBottom: 12, marginLeft: 8 }}>
            Call protected /api/user/me (uses JWT)
          </button>

          <div style={{ marginBottom: 16 }}>
            <div style={{ fontFamily: 'monospace', whiteSpace: 'pre-wrap' }}>{status}</div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
            <form onSubmit={registerUser} style={{ border: '1px solid #444', padding: 12, borderRadius: 8 }}>
              <h3>Register</h3>
              <label>
                Username
                <input
                  value={register.username}
                  onChange={(e) => setRegister({ ...register, username: e.target.value })}
                />
              </label>
              <label>
                Password
                <input
                  type="password"
                  value={register.password}
                  onChange={(e) => setRegister({ ...register, password: e.target.value })}
                />
              </label>
              <label>
                Sex
                <select
                  value={register.sex}
                  onChange={(e) => setRegister({ ...register, sex: e.target.value })}
                >
                  <option value="M">M</option>
                  <option value="F">F</option>
                </select>
              </label>
              <label>
                Height (cm)
                <input
                  type="number"
                  value={register.height}
                  onChange={(e) => setRegister({ ...register, height: Number(e.target.value) })}
                />
              </label>
              <label>
                Weight (lbs)
                <input
                  type="number"
                  value={register.weight}
                  onChange={(e) => setRegister({ ...register, weight: Number(e.target.value) })}
                />
              </label>
              <label>
                Activity Level
                <input
                  value={register.activityLevel}
                  onChange={(e) => setRegister({ ...register, activityLevel: e.target.value })}
                />
              </label>
              <label>
                Goal
                <input
                  value={register.goal}
                  onChange={(e) => setRegister({ ...register, goal: e.target.value })}
                />
              </label>
              <label>
                Goal Weight Change / Week (lbs)
                <input
                  type="number"
                  step="0.5"
                  value={register.goalWeightChangePerWeek}
                  onChange={(e) =>
                    setRegister({ ...register, goalWeightChangePerWeek: Number(e.target.value) })
                  }
                />
              </label>
              <button type="submit">Register</button>
            </form>

            <form onSubmit={loginUser} style={{ border: '1px solid #444', padding: 12, borderRadius: 8 }}>
              <h3>Login</h3>
              <label>
                Username
                <input
                  value={login.username}
                  onChange={(e) => setLogin({ ...login, username: e.target.value })}
                />
              </label>
              <label>
                Password
                <input
                  type="password"
                  value={login.password}
                  onChange={(e) => setLogin({ ...login, password: e.target.value })}
                />
              </label>
              <button type="submit">Login</button>
            </form>
          </div>
        </div>
      </header>
    </div>
  );
}

export default App;
