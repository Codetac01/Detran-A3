import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    public static void main(String[] args) throws SQLException {

        BancoDeDados.inicializarBanco();
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== SISTEMA DE GERENCIAMENTO DE VEÍCULOS ===");
            System.out.println("1. Cadastrar novo veículo");
            System.out.println("2. Converter placa para Mercosul");
            System.out.println("3. Transferir propriedade");
            System.out.println("4. Consultar banco de dados");
            System.out.println("5. Verificar todos os veículos");
            System.out.println("6. Sair");
            System.out.print("Opção: ");

            int op = scan.nextInt();

            switch (op) {
                case 1:
                    Carro.setCarro();
                    break;
                case 2:
                    Mercosul.converterPlaca();
                    break;
                case 3:
                    Carro.transferencia();
                    break;
                case 4:
                    Historico.consultarBancoDeDados();
                    break;
                case 5:
                    Historico.verificarBancoDeDados();
                    break;
                case 6:
                    System.out.println("Sistema encerrado.");
                    scan.close();
                    return;
                default:
                    System.out.println("Operação inválida.");
            }
        }
    }
}
