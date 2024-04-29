package repositorio;

import modelo.Hospital;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class HospitalRepositorio {
    
    final JdbcTemplate conn;// simulando conexão com BD
    
    public HospitalRepositorio(JdbcTemplate conn)
    {
        this.conn = conn;
    };

    public Hospital buscarHospitalPorId(int id){
        return conn.queryForObject("SELECT * FROM hospital WHERE idHospital = ?;", new BeanPropertyRowMapper<>(Hospital.class), id);
    }

    public Hospital autenticarHospital(String cnpj,String senha){
        DepartamentoRepositorio departamentoRepositorio = new DepartamentoRepositorio(conn);
       Hospital hospital = conn.queryForObject("select * from hospital WHERE senha = '" + senha + "' and cnpj = '" + cnpj +"'",new BeanPropertyRowMapper<>(Hospital.class));
       hospital.setDepartamentos(departamentoRepositorio.buscarDepartamentoHospital(hospital.getIdHospital()));
       return hospital;
    }
}
