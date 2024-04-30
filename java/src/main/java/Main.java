import Persistencia.Conexao;
import com.github.britooo.looca.api.group.janelas.Janela;
import modelo.Departamento;
import modelo.Hospital;
import org.springframework.jdbc.core.JdbcTemplate;
import repositorio.ComputadorRepositorio;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import modelo.Computador;
import repositorio.HospitalRepositorio;

public class Main {
    static Scanner leitorStr = new Scanner(System.in);
    static Conexao conexao = new Conexao();
    static JdbcTemplate conn = conexao.getConn();


    public static void main(String[] args) throws InterruptedException {

        telaInicial();
    }

    static void telaInicial() throws InterruptedException {

        System.out.println("""
                         
                         :JJJ~        !JJ?.      !JJJJJJJJJ?:     :JJJJJJJJJ7~:      !JJJJJJJJJJJ~    .?JJJJJJJJJ7.      .^7?JJJJJ?!:     .?J!       ^JJ:            \s
                         :YYYY~     .7YYYJ.      75J:.......      :YY!....:^7YY7.    ....:J57.....    :J57........      ~JY?~:..:^!?~     .J5!       ^YY:            \s
                         :YY!?Y!   .757!YJ.      75?:......       :YY!       :J57.       .J5!         :JY!.......      ~YY~               .JY7.......~YY:            \s
                         :YY^.J5! .?57 ~YJ.      7YYJJJJJJJ^      :YY!        !YJ:       .J5!         :JYYJJJJJJ?.     75?.               .JYYJJJJJJJJYY:            \s
                         :YY^ .?Y?JY!  !YJ.      75?:......       :YY!       :J57.       .J5!         :JY!.......      ~YY~               .JY7:::::::~YY:            \s
                         :YY^  .?YY!   !YJ.      75?:........     :YY!....:^!YY7.        .J5!         :J5!........      ~JY?~:...^!?^     .J5!       ^YY:            \s
                         :JJ^   .::    ~Y?.      !YJJJJJJJJJ~     :JYJJJJJJJ?~:          .?Y!         :?YJJJJJJJJ?:      .~7JJJJJJ?~:     .?Y!       ^YY:            \s
                """);
        System.out.println("""
                BEM-VINDO(A) AO NOSSO SISTEMA DE MONITORAMENTO DE COMPUTADORES!
                """);
        cadastrar();
    }

    public static void cadastrar(){
        System.out.println("Digite seu cnpj");
        String cnpjNovo = leitorStr.next();
        System.out.println("Digite sua senha");
        String senhaNovo = leitorStr.next();


        HospitalRepositorio hospitalRepositorio = new HospitalRepositorio(conn);
        hospitalRepositorio.autenticarHospital(cnpjNovo, senhaNovo);
       Hospital hospitalNovo = hospitalRepositorio.autenticarHospital(cnpjNovo, senhaNovo);
        System.out.println(hospitalNovo);

        for(Departamento departamento : hospitalNovo.getDepartamentos()){
            System.out.println(departamento);
        }
        Departamento departamento1= null;
        while(departamento1 == null){
            System.out.println("Digite um departamento");
           Integer escolherDepartamento = leitorStr.nextInt();
           for(Departamento departamento : hospitalNovo.getDepartamentos()){
               if(departamento.getIdDepartamento() == escolherDepartamento){
                   departamento1 = departamento;
                   break;
               }
           }
        }

        Computador computador = new Computador();
        computador.setFkDepartamento(departamento1.getIdDepartamento());
        computador.setFkHospital(hospitalNovo.getIdHospital());
        System.out.println(departamento1);
        System.out.println("Digite o nome do computador");
        computador.setnome(leitorStr.next());



        System.out.println("Digite o codigo de patrimonio");
        computador.setCodPatrimonio(leitorStr.next());

        System.out.println("Digite a senha:");
        String senha = leitorStr.next();


        ComputadorRepositorio computadorRepositorio = new ComputadorRepositorio(conn);
        computadorRepositorio.cadastrarComputador(computador, senha);

    }
    static void login() throws InterruptedException {
        System.out.println("Login iniciado! \n");

        ComputadorRepositorio repositorioComputador = new ComputadorRepositorio(conn);

        List computadorAutenticado;
        do {
            System.out.println("Código do patrimônio:");
            String codPatrimonio = leitorStr.next();
            String senhaH = leitorStr.next();
            System.out.println("senha:");

            computadorAutenticado = repositorioComputador.autenticarComputador(senhaH, codPatrimonio);

            if (computadorAutenticado.size() != 1) {
                System.out.println("Código do patrimônio ou senha incorreta. \nPor favor, tente novamente. \n");
            }

        } while (computadorAutenticado.size() != 1);

        System.out.println("""
                \n
                Login realizado com sucesso!
                Estes são os dados da conta acessada:
                \n
                """);

        Computador computador = (Computador) computadorAutenticado.get(0);
        System.out.println(computador);

        System.out.println("\nAGORA ESTE COMPUTADOR ESTÁ SENDO MONITORADO EM TEMPO REAL.");

        inserirLeituras(computador);
    }

    public static void inserirLeituras(Computador computador) throws InterruptedException {

        for (int i = 1; true; i++) {

            String queryRamCpu = "INSERT INTO leituraRamCpu (ram, cpu, dataLeitura, fkComputador, fkDepartamento, fkHospital) VALUES("
                    + computador.getPorcentagemConsumoMemoria()
                    + ", " + computador.getPorcentagemConsumoCpu()
                    + ", '" + LocalDateTime.now() + "', " + computador.getIdComputador()
                    + ", " + computador.getFkDepartamento() + ", "
                    + computador.getFkHospital() + ");";

            System.out.printf("""
                    COMANDO DE INSERÇÃO DE LEITURAS DE RAM E CPU:
                    %s \n
                    """, queryRamCpu);
            conn.execute(queryRamCpu);

            for (Janela janela : computador.getJanelas()) {
                String queryFerramenta =
                        "INSERT INTO leituraFerramenta (nomeApp, dtLeitura, caminho, fkComputador, fkDepartamento, fkHospital) VALUES( '"
                                + janela.getTitulo() + "', '"
                                + LocalDateTime.now() + "', '"
                                + janela.getComando() + "', "
                                + computador.getIdComputador() + ", "
                                + computador.getFkDepartamento() + ", "
                                + computador.getFkHospital() + ");";

                System.out.printf("""
                        COMANDO DE INSERÇÃO DE LEITURAS DE FERRAMENTAS EM USO: \n
                        %s \n
                        """, queryFerramenta);
                conn.execute(queryFerramenta);
            }

            if (i > 9) {
                String queryDisco = "INSERT INTO leituraDisco (disco, dataLeitura, fkComputador, fkDepartamento, fkHospital) VALUES ("
                        + computador.getDiscoComMaisConsumo(computador.getPorcentagemDeTodosVolumes())
                        + ", '" + LocalDateTime.now() + "', " + computador.getIdComputador()
                        + ", " + computador.getFkDepartamento() + ", " + computador.getFkDepartamento() + ");";

                System.out.printf("""
                        COMANDO DE INSERÇÃO DE LEITURAS DE FERRAMENTAS EM USO: \n
                        %s \n
                        """, queryDisco);
                conn.execute(queryDisco);

                i = 0;
            }

            Thread.sleep(3000);
        }
    }
}