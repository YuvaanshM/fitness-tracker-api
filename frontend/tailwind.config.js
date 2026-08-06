/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#15221b',
        cream: '#f7f8f2',
        brand: {
          50: '#eefbf2',
          100: '#d8f5e0',
          500: '#35a85c',
          600: '#258848',
          700: '#206d3d',
          900: '#19472c',
        },
      },
      boxShadow: {
        card: '0 18px 45px -28px rgba(21, 34, 27, 0.35)',
      },
    },
  },
  plugins: [],
};
