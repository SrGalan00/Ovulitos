import React, { useState } from 'react';

export const Register: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [rememberme, setRememberme] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log({ username, password, rememberme });
  };

  return (
    <>
      <style>{`
        html,
        body,
        #root {
            width: 100%;
            height: 100%;
            margin: 0;
            background: #FFF8C9;
        }


        .login-page {
          min-height: 100vh;
          display: flex;
          justify-content: center;
          align-items: center;
          background: #FFF8C9;
        }

        .circle {
          width: 800px;
          height: 800px;
          border-radius: 50%;
          position: relative;
          background: #FFF8C9; /* rosa suave */
        }

        /* Imagen de fondo (si no existe, queda el color) */
        .circle::before {
          content: '';
          position: absolute;
          inset: 40px;
          border-radius: 50%;
          background: 
            url('/Ovulito.png') center/cover no-repeat,
            linear-gradient(180deg, #f7b6c8, #f3a2b9);
        }

        .form-overlay {
          position: absolute;
          inset: 0;
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          gap: 22px;
          z-index: 2;
        }

        .label{
          width: 320px;
          padding: 3px 6px;
          font-size: 16px;
          outline: none;
        }

        .input {
          width: 320px;
          padding: 14px 16px;
          border-radius: 12px;
          border: 1px solid #ddd;
          font-size: 16px;
          outline: none;
        }

        .input:focus {
          border-color: #c45a7a;
        }

        .remember {
          display: flex;
          align-items: center;
          gap: 10px;
          color: #333;
          font-size: 14px;
          font-weight: 500;
        }

        .remember input {
          width: 16px;
          height: 16px;
        }

        .button {
          margin-top: 20px;
          width: 200px;
          padding: 14px;
          border-radius: 14px;
          border: none;
          background: #ffffff;
          font-weight: 700;
          cursor: pointer;
          border: 1px solid #ddd;
        }

        .button:hover {
          background: #f1f1f1;
        }
      `}</style>

      <div className="login-page">
        <div className="circle">
          <form className="form-overlay" onSubmit={handleSubmit}>
          <label className='label'>Username</label>
            <input
              className="input"
              placeholder="Email"
              value={username}
              onChange={e => setUsername(e.target.value)}
            />

            <label className='label'>Username</label>
            <input
              className="input"
              placeholder="Username"
              value={username}
              onChange={e => setUsername(e.target.value)}
            />

            <label>Password</label>
            <input
              className="input"
              type="password"
              placeholder="Password"
              value={password}
              onChange={e => setPassword(e.target.value)}
            />

            <label className="remember">
              <input
                type="checkbox"
                checked={rememberme}
                onChange={e => setRememberme(e.target.checked)}
              />
              Remember me
            </label>

            <button className="button" type="submit">
              LOGIN
            </button>
          </form>
        </div>
      </div>
    </>
  );
};
