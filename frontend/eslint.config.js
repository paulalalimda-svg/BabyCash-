// eslint.config.js
import js from '@eslint/js';
import pluginTs from '@typescript-eslint/eslint-plugin';
import parserTs from '@typescript-eslint/parser';
import reactPlugin from 'eslint-plugin-react';
import jsxA11y from 'eslint-plugin-jsx-a11y';
import prettier from 'eslint-plugin-prettier';
import globals from 'globals';

export default [
  {
    ignores: ['dist/**'],
  },
  {
    files: ['**/*.{ts,tsx,js,jsx}'],
    languageOptions: {
      parser: parserTs,
      parserOptions: {
        ecmaVersion: 'latest',
        sourceType: 'module',
        ecmaFeatures: { jsx: true },
      },
      globals: {
        ...globals.browser,
        ...globals.es2021,
      },
    },
    plugins: {
      '@typescript-eslint': pluginTs,
      react: reactPlugin,
      'jsx-a11y': jsxA11y,
      prettier: prettier,
    },
    rules: {
      ...js.configs.recommended.rules,
      ...pluginTs.configs.recommended.rules,
      ...reactPlugin.configs.recommended.rules,
      ...jsxA11y.configs.recommended.rules,

      // 🚀 Ajustes clave para React 17+
      'react/react-in-jsx-scope': 'off',
      'react/prop-types': 'off',
      'no-undef': 'off',

      // Opcional: desactiva esta si no quieres arreglar labels todavía
      'jsx-a11y/label-has-associated-control': 'off',

      // 🚀 Prettier (dobles comillas + soporta Windows)
      'prettier/prettier': [
        'warn',
        {
          singleQuote: true, // usa comillas dobles
          semi: true,
          endOfLine: 'auto',
        },
      ],
    },
    settings: {
      react: {
        version: 'detect',
      },
    },
  },
];
