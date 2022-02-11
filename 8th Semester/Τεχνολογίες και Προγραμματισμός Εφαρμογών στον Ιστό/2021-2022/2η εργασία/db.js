const {Pool} = require('pg');

//instantiate db connection
const pool = new Pool({
    user: 'postgres',
    database: 'webdev',
    password: 'postgres',
    port: 5432,
    host: 'localhost',
  });
  
  module.exports = { pool };