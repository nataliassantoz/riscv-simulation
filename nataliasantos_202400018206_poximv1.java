import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import poxim.Instrucoes;
//import poxim.Memoria;
//import poxim.Processador;
//import utils.Leitura;

public class nataliasantos_202400018206_poximv1 {

	public static void main(String[] args) {
		
		String caminhoArquivoEntrada = args[0];
		String caminhoArquivoSaida = args[1];
		
	    String instrucoesHexadecimal = Leitura.readFromFile(caminhoArquivoEntrada);
	    
	    if (instrucoesHexadecimal.isEmpty()) {
            System.out.println("Erro: Arquivo de entrada vazio ou não encontrado.");
            return;
        } 
	    
	    List<Instrucoes>  listaDeInstrucoes = listarInstrucoes(instrucoesHexadecimal);
	    Leitura.writeToFile(caminhoArquivoSaida, "");
   	    
	    Memoria memoria = new Memoria();
	    
	    long off0 = 0;
	    for(Instrucoes item : listaDeInstrucoes) {
//	    	System.out.println("valor das instrucoes -->" + item.instrucoes); 
//	    	System.out.println("valor do offset -->" + item.offset); 	
	    	
	 	    memoria.carregarInstrucoes2(item.instrucoes, item.offset);
	 	    
	    } 
	    Processador processador = new Processador(caminhoArquivoSaida, listaDeInstrucoes.get(0).offset, memoria);
	    processador.processarInstrucoes();
	}
	//leitura do offset
	public static Long lerOffSet(String entrada) {
		
	    String[] linhas = entrada.split("\n");
	    String offset = linhas[0].trim().replace("@", "");
	    
	    try {
	        return Long.parseLong(offset, 16);
	    } catch (NumberFormatException e) {
	        System.out.println("Erro ao converter para Long: " + e.getMessage());
	        return null;  
	    }
	}
	
	public static  List<Instrucoes> listarInstrucoes(String entrada) {
        String[] linhas = entrada.split("\n");

        List<Instrucoes> instrucoes = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String offsetAtual = null;

        for (String linha : linhas) {
            linha = linha.trim();

            if (linha.startsWith("@")) { 
            	
                // Salva a instrução anterior antes de trocar de offset
                if (offsetAtual != null && sb.length() > 0) {
                    Instrucoes instrucao = new Instrucoes();
                    instrucao.offset = Long.parseLong(offsetAtual, 16);
                    instrucao.instrucoes = sb.toString().trim();
                    instrucoes.add(instrucao);
                    sb.setLength(0); // Reseta o buffer
                }

                offsetAtual = linha.replace("@", "").trim();
            } else {
                sb.append(linha).append(" ");
            }
        }

        // Adiciona a última instrução pendente
        if (offsetAtual != null && sb.length() > 0) {
            Instrucoes instrucao = new Instrucoes();
            instrucao.offset = Long.parseLong(offsetAtual, 16);
            instrucao.offsetString = offsetAtual;
            instrucao.instrucoes = sb.toString().trim();
            instrucoes.add(instrucao);
        }

        return instrucoes;
    }
}

class Leitura {

    public static String readFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
    // Método para escrever em um arquivo
    public static void writeToFile(String fileName, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
            if(!content.isEmpty()) {
            	writer.newLine(); // Adiciona uma quebra de linha no final
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para adicionar conteúdo ao final do arquivo (modo append)
    public static void appendToFile(String fileName, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(content);
            writer.newLine(); // Adiciona uma nova linha ao final
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
 class Instrucoes {
    public Long offset;
    public String offsetString;
    public String instrucoes;

}

class Memoria {
	
	public   byte[] memoria = new byte[32 * 1024]; 
	private  int qdtInstrucoes;
	private  long offset;  //precisa ser constante, para realizar os calculos****
	private String entrada;
	
	public Memoria() {
		
	}
	public  byte[] getMemoria() {return memoria;}
	

	public  int getQdtInstrucoes() {
		return qdtInstrucoes;
	}

	public  long getOffset() {
		return offset;
	}

	public String getEntrada() {
		return entrada;
	}
	
	public void runMemoria() {
//		String instrucoes = obterInstrucoes(entrada);
		
//       carregarInstrucoes(entrada);

	}

	public  String obterInstrucoes(String entrada) {
        
        String[] linhas = entrada.split("\n");

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < linhas.length; i++) {
        	sb.append(linhas[i].trim()).append(" ");
        }
        return sb.toString().trim();
    }

    public  void carregarInstrucoes(String instrucoes, Long offset) {
    	//System.out.println("chegou ate aqui");
        instrucoes = instrucoes.replaceAll("[\\s\\n]+", " ").trim();
        System.out.println("instrucoes" + instrucoes);
        
        String[] vetorHexadecimal = instrucoes.split(" ");
       
       long hex = offset;
       
        for (int i = 0; i < vetorHexadecimal.length; i += 4) {
            if (i + 3 < vetorHexadecimal.length) {
                try {
                    String instrucaoHex = vetorHexadecimal[i] + //6F
                            vetorHexadecimal[i + 1] +			//00
                            vetorHexadecimal[i + 2] +			//00
                            vetorHexadecimal[i + 3];			//0A
                    byte[] instrucaoEmBytes = inverterBytes(instrucaoHex);
                    // Armazena os 4 bytes na mem 
                    	for (int j = 0; j < 4; j++) {
                    	 int enderecoBaixo = (int)((hex - offset) & 0xFFFFFFFFL); 
                    	  memoria[  enderecoBaixo ] = instrucaoEmBytes[j];
                    	  int instrucao = memoria[enderecoBaixo ];
                    	  //System.out.printf(" %02x%n", instrucaoEmBytes[j]);
                    	  //System.out.println("hex " + hex);
                    	  
                    	  //System.out.println("endBaixo " + enderecoBaixo ); 
                    	  
                    	  hex++;
                    	}
                    	//System.out.printf("offset %08x%n ", hex - offset);
                    	//System.out.println("------------");
                    	
                }
                finally {
                }
               }
            }
        }
    
    
    public   void carregarInstrucoes2(String instrucoes, long offset) {
        instrucoes = instrucoes.replaceAll("[\\s\\n]+", " ").trim();
        String[] vetorHexadecimal = instrucoes.split(" ");
        
        long enderecoAtual = offset %  memoria.length; // Começa no *offset* fornecido
        
//        if (enderecoAtual > 3) {
////        	enderecoAtual += 3;
//        }
        
        
        
        
        
        for (int i = 0; i < vetorHexadecimal.length; i += 4) {
            if (i + 3 < vetorHexadecimal.length) {
                try {
                    // Concatena os 4 bytes na ordem original
                    String instrucaoHex = vetorHexadecimal[i] + 
                                          vetorHexadecimal[i + 1] + 
                                          vetorHexadecimal[i + 2] + 
                                          vetorHexadecimal[i + 3];
                    
                    byte[] instrucaoEmBytes = inverterBytes(instrucaoHex); // Converte para *little-endian*
                    
                  

                    
                    // Calcula o índice na memória com base no *offset*
                    int enderecoMemoria = (int) (enderecoAtual );
                    
                    // Armazena os 4 bytes na memória
                    for (int j = 0; j < 4; j++) {
                        memoria[enderecoMemoria + j] = instrucaoEmBytes[j];
                        //String intrucaoMem = String.format("enedreo memoria: 0x%08x -> 0x%08x", enderecoMemoria + j,  memoria[enderecoMemoria + j]);
                        //System.out.println(intrucaoMem);
                    }
                    
                    enderecoAtual += 4; // Avança 4 bytes para a próxima instrução
                    
                } catch (Exception e) {
                    System.err.println("Erro ao carregar instrução na memória: " + e.getMessage());
                }
            }
        }
        
        ;
        
        
    }
    
    

    
    
    public static byte[] inverterBytes(String instrucaoHex) { //6F 00 00 0AV  --- > 0A 00 00 6F
    	//inverte -- little endian
        byte[] bytes = new byte[4];
        bytes[0] = (byte) Integer.parseInt(instrucaoHex.substring(0, 2), 16);
        bytes[1] = (byte) Integer.parseInt(instrucaoHex.substring(2, 4), 16);
        bytes[2] = (byte) Integer.parseInt(instrucaoHex.substring(4, 6), 16);
        bytes[3] = (byte) Integer.parseInt(instrucaoHex.substring(6, 8), 16); 
        return bytes;
    }
}
 class Processador {
	private static int qdtInstrucoes;
	private static int[] registradores;
	private static byte[] memoria; 
	private static long offset;
	
	private static long offsets[];
	private static long PC;
	private StringBuilder logBuilder; 
	private String caminhoArquivoSaida;
	
	public Processador(String caminhoArquivoSaida,Long offset, Memoria memoria) {
		this.caminhoArquivoSaida = caminhoArquivoSaida;
		Processador.memoria = memoria.getMemoria();
		Processador.qdtInstrucoes = memoria.getQdtInstrucoes();
		Processador.registradores  = new int[32];
		Arrays.fill(registradores, 0);
		Processador.PC= offset;
		Processador.offset = offset;
		logBuilder = new StringBuilder(); 
	}

    public void processarInstrucoes() {
    	int loop =1;
    	int k = 0;
    	boolean run = true;
    	
    	boolean pcUpdated;
    	while(run) {
    		
    		 pcUpdated = false; 
    		 
    		int instruction =   ((memoria[(int)((PC + k   - offset) & 0xFFFFFFFFL)] & 0xFF))   |
                    			((memoria[(int)((PC + k+1 - offset) & 0xFFFFFFFFL)] & 0xFF) << 8) |
                    			((memoria[(int)((PC + k+2 - offset) & 0xFFFFFFFFL)] & 0xFF) << 16)  |
                    			((memoria[(int)((PC + k+3 - offset) & 0xFFFFFFFFL)] & 0xFF)) << 24; 
    	
    		/*System.out.println();
    		System.out.println("-------------------------------------------------");
    		System.out.printf("valor de pc no inicio: 0x%08x%n", PC);
    		System.out.println(loop);
    		System.out.printf("instrucao em hexadecimal 0x%08x: ", instruction);
    		String instructionBinario = String.format("%32s", Integer.toBinaryString(instruction)).replace(' ', '0');
    		System.out.println(instructionBinario);//63 F0 F1 00 			63 F2 2D 01			   //00 f1 f0 63            01 2d f2 63
    		System.out.println();
    		*/
    		int opcode = instruction & 0b1111111;   
            switch(opcode) {
	            	case 0b1101111: //JAL
	            		pcUpdated = false;
	            		
	            		int rdJal = (instruction >> 7) & 0b11111;
	                    int imm20 = (instruction >> 31) & 0x1; 
	                    int imm10_1 = (instruction >> 21) & 0x3FF; 
	                    int imm11 = (instruction >> 20) & 0x1; 
	                    int imm19_12 = (instruction >> 12) & 0xFF; 
	                   
	                    
	                    int immediateJal = (imm20 << 20) | (imm19_12 << 12) | (imm11 << 11) | (imm10_1 << 1);

	                    if ((immediateJal & (1 << 20)) != 0) {  
	                        immediateJal |= 0xFFF00000;  // Corrige para cobrir todos os bits superiores
	                    }
	                    
	                    if (rdJal != 0) { 
	                    	registradores[rdJal] = (int) (PC + 4);
	                    }
	                    int valorRepresentado = (int) (PC + 4);
	                    
	                    long novoPC = (PC + immediateJal) & 0xFFFFFFFFL;  // Mantém o valor dentro de 32 bits
	                    
//			                    int immediateFormatado = immediateJal >> 1; 
//			                    long novoPC = PC + immediateJal;
	                    int imediatoParaPrint = immediateJal >> 1;  
		                String imediatoFormatado;
		                 
		                 if (imediatoParaPrint < 0) {
		                     imediatoFormatado = String.format("0x%05x", imediatoParaPrint & 0xFFFFF);  
		                 }
		                 else {
		                     imediatoFormatado = String.format("0x%05x", imediatoParaPrint);  
		                 }

	                    String enderecoHexJal = String.format("0x%08x", (int) PC);  
	                    String enderecoNovoPC = String.format("0x%08x", (int) novoPC); 
	                    String nomeRd = getRegistroLabel(rdJal);
	                    logBuilder.append(String.format("%s:jal    %s,%s          pc=%s,%s=0x%08x%n", 
	                    	    enderecoHexJal, nomeRd, imediatoFormatado, enderecoNovoPC, nomeRd, valorRepresentado));
	                    
//	                    System.out.printf("%s:jal    %s,%s        pc=%s,%s=0x%08x%n", 
//	                    	    enderecoHexJal, nomeRd, imediatoFormatado, enderecoNovoPC, nomeRd, valorRepresentado);
	                    PC = novoPC;
	                    pcUpdated = true;
	                    break;
                   
                    case 0b1100111:  // JALR  
                    	pcUpdated = false;
                        int rdJalr = (instruction >> 7) & 0b11111;  
                        int rs1Jalr = (instruction >> 15) & 0b11111;  

                        int immJalr = (instruction >> 20) & 0xFFF;
                        
                        if ((immJalr & 0x800) != 0) {  
                            immJalr |= 0xFFFFF000;  
                        }
                       
                        // Calcula o endereço de destino: endereço base + imediato
                        long enderecoDestino = (registradores[rs1Jalr] + immJalr) & ~1;  // O endereço de destino deve ser alinhado a 2 bytes
                        
                        int valorRetorno = (int) (PC + 4);
                        pcUpdated = true;
                        
                        // Armazena o valor do endereço de retorno no registrador de destino
                        if (rdJalr != 0) { 
                        	registradores[rdJalr] = (int) (PC + 4);
                        }
                        
                        // Atualiza o PC com o endereço de destino calculado
                        long pcAnterior = PC;
                        PC = enderecoDestino;

                        // Impressão do resultado após a atualização correta de PC
                        String enderecoHexJalr = String.format("0x%08x", pcAnterior & 0xFFFFFFFFL);  // Valor de PC antes da atualização
                        String nomeRdJalr = getRegistroLabel(rdJalr); //zero
                        String nomeRs1Jalr = getRegistroLabel(rs1Jalr);
                        String valorImediatoJalr = String.format("0x%03x", immJalr);
                        String enderecoDestinoHex = String.format("0x%08x", (int) enderecoDestino);
                        int valorPrint = (rdJalr == 0) ? valorRetorno : registradores[rdJalr];
                        logBuilder.append(String.format("%s:jalr   %s,%s,%s     pc=%s+%s,%s=0x%08x%n", 
                                enderecoHexJalr, nomeRdJalr, nomeRs1Jalr, valorImediatoJalr, 
                                enderecoDestinoHex, "0x00000000", nomeRdJalr, valorPrint));
//                        System.out.printf("%s:jalr   %s,%s,%s     pc=%s+%s,%s=0x%08x%n", 
//                                enderecoHexJalr, nomeRdJalr, nomeRs1Jalr, valorImediatoJalr, 
//                                enderecoDestinoHex, "0x00000000", nomeRdJalr, valorPrint);
                        break;

            	case 0b0110011: //R-TYPE
	            		pcUpdated = false;
	            		int funct3 = (instruction >> 12) & 0b111;
	            		int funct7 = (instruction >> 25) & 0b1111111;
	                    int rs1    = (instruction >> 15) & 0b11111;
	                    int rs2    = (instruction >> 20) & 0b11111;
	                    int rd     = (instruction >>  7) & 0b11111;
		                  
	            		if(funct3 == 0b111 && funct7 == 0b0000000) { //AND
	            	        int resultado = registradores[rs1] & registradores[rs2];
	            	        
		            			if (rd != 0) {
		            				registradores[rd] = resultado;
		            			}
		            			
		            			String enderecoHex = String.format("0x%08x", PC + k);
		                        String rdLabel = getRegistroLabel(rd);
		                        String rs1Label = getRegistroLabel(rs1);
		                        String rs2Label = getRegistroLabel(rs2);
		                        String rs1Valor = String.format("0x%08x", registradores[rs1]);
		                        String rs2Valor = String.format("0x%08x", registradores[rs2]);
		
		                        String rdValor = (rd != 0) ? String.format("0x%08x", registradores[rd]): String.format("0x%08x", resultado);
		                        
		                        logBuilder.append(String.format("%s:and    %s,%s,%s    %s=%s&%s=%s\n",enderecoHex, rdLabel, rs1Label, rs2Label,rdLabel, rs1Valor, rs2Valor, rdValor));
		                        //System.out.printf("%s:and    %s,%s,%s    %s=%s&%s=%s\n",enderecoHex, rdLabel, rs1Label, rs2Label,rdLabel, rs1Valor, rs2Valor, rdValor);
	            		}
	            		else if (funct3 == 0b110 && funct7 == 0b0000000) { //OR
	            		    int resultado = registradores[rs1] | registradores[rs2];
        						if (rd != 0) {
        							registradores[rd] = resultado;
        						}

        					    String enderecoHex = String.format("0x%08x", PC + k);
        					    String rdLabel = getRegistroLabel(rd);
        					    String rs1Label = getRegistroLabel(rs1);
        					    String rs2Label = getRegistroLabel(rs2);
        					    String rs1Valor = String.format("0x%08x", registradores[rs1]);
        					    String rs2Valor = String.format("0x%08x", registradores[rs2]);
        					    String rdValor = (rd != 0)
        					            ? String.format("0x%08x", registradores[rd])
        					            : String.format("0x%08x", resultado);
        					    
        					    logBuilder.append(String.format("%s:or     %s,%s,%s          %s=%s|%s=%s\n", enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor));
        					    //System.out.printf("%s:or     %s,%s,%s          %s=%s|%s=%s\n", enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor);
	            		}
	            		else if(funct3 == 0b100 && funct7 == 0b0000000) { //XOR
	            		    int resultado = registradores[rs1] ^ registradores[rs2];
        						if (rd != 0) {
        							registradores[rd] = resultado;
        						}
        						String enderecoHex = String.format("0x%08x", PC + k);
        						String rdLabel = getRegistroLabel(rd);
        						String rs1Label = getRegistroLabel(rs1);
        						String rs2Label = getRegistroLabel(rs2);
        						String rs1Valor = String.format("0x%08x", registradores[rs1]);
        						String rs2Valor = String.format("0x%08x", registradores[rs2]);
        						String rdValor = (rd != 0)
        					            ? String.format("0x%08x", registradores[rd])
        					            : String.format("0x%08x", resultado);
        						logBuilder.append(String.format("%s:xor    %s,%s,%s            %s=%s^%s=%s\n", enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor));
        						//System.out.printf("%s:xor    %s,%s,%s            %s=%s^%s=%s\n", enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor);
	            		}
	            		else if (funct3 == 0b001 && funct7 == 0b0000000) { //SLL
	             
	            	        int shiftAmount = registradores[rs2] & 0b11111; // Obtém os 5 bits menos significativos de rs2
						    int resultado = registradores[rs1] << shiftAmount; // Calcula o resultado do shift à esquerda
            			    if (rd != 0) {
            			        registradores[rd] =resultado;
            			    }
            			    
            			    String enderecoHex = String.format("0x%08x", PC + k);
            			    String rdLabel = getRegistroLabel(rd);
            			    String rs1Label = getRegistroLabel(rs1);
            			    String rs2Label = getRegistroLabel(rs2);
            			    
            			    String rs1Valor = String.format("0x%08x", registradores[rs1]);
            			    String resultadoValor = String.format("0x%08x", resultado);
//
            			    logBuilder.append(String.format("%s:sll    %s,%s,%s            %s=%s<<%d=%s\n",
            			            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, shiftAmount, resultadoValor));
//            			    System.out.printf("%s:sll    %s,%s,%s            %s=%s<<%d=%s\n",
//            			            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, shiftAmount, resultadoValor);

	            		}
	            		else if(funct3 == 0b101 && funct7 == 0b0000000) { //SRL

    				        	int deslocamento = registradores[rs2] & 0x1F;       
    				        	int original = registradores[rs1]; 
    				        	int resultado = original >>> deslocamento;   
   
	            			    if (rd != 0) {
	            			        registradores[rd] = resultado;
	            			    }

	            			    String enderecoHex = String.format("0x%08x", PC + k);
	            			    String rdLabel = getRegistroLabel(rd);
	            			    String rs1Label = getRegistroLabel(rs1);
	            			    String rs2Label = getRegistroLabel(rs2); 

	            			    String rs1Valor = String.format("0x%08x", original);
	            			    String resultadoValor = String.format("0x%08x", resultado);
	            			    
	            			    logBuilder.append(String.format("%s:srl    %s,%s,%s            %s=%s>>%d=%s\n",
	            			            enderecoHex, rdLabel, rs1Label, rs2Label,
	            			            rdLabel, rs1Valor, deslocamento, resultadoValor));
//	            			    System.out.printf("%s:srl    %s,%s,%s            %s=%s>>%d=%s\n",
//	            			            enderecoHex, rdLabel, rs1Label, rs2Label,
//	            			            rdLabel, rs1Valor, deslocamento, resultadoValor);

	            		}
	            		else if(funct3 == 0b010 && funct7 == 0b0000000) { //SLT

	            			int rs1Valor = registradores[rs1] & 0xFFFFFFFF; // Mantém 32 bits
	            		    int rs2Valor = registradores[rs2] & 0xFFFFFFFF; // Mantém 32 bits
	            		    
	            		    int resultado = (rs1Valor < rs2Valor) ? 1 : 0;
	            		    
	            		    if (rd != 0) {
	            		        registradores[rd] = resultado;
	            		    }

	            		    String enderecoHex = String.format("0x%08x", PC + k);
	            		    String rdLabel = getRegistroLabel(rd);
	            		    String rs1Label = getRegistroLabel(rs1);
	            		    String rs2Label = getRegistroLabel(rs2);
	            		    String rs1Valor1 = String.format("0x%08x", rs1Valor );
	            		    String rs2Valor1 = String.format("0x%08x", rs2Valor);
	            		    
	            		    String resultadoValor = String.format("%d", resultado);

	            		    logBuilder.append(String.format("%s:slt    %s,%s,%s            %s=(%s<%s)=%s\n",
	            		            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor1, rs2Valor1, resultadoValor));

//	            		    System.out.printf("%s:slt    %s,%s,%s            %s=(%s<%s)=%s\n",
//	            		            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor1, rs2Valor1, resultadoValor);
	  
	            		}
	            		else if(funct3 == 0b011 && funct7 == 0b0000000) {//STLU	

	            			long rs1Value = registradores[rs1] & 0xFFFFFFFFL; 
            			    long rs2Value = registradores[rs2] & 0xFFFFFFFFL;
            			    
            			    int resultado = (rs1Value < rs2Value) ? 1 : 0;

            			    if (rd != 0) {
            			        registradores[rd] = resultado;
            			    }

            			    String enderecoHex = String.format("0x%08x", PC + k);
            			    String rdLabel = getRegistroLabel(rd);
            			    String rs1Label = getRegistroLabel(rs1);
            			    String rs2Label = getRegistroLabel(rs2);
            			    String rs1Valor = String.format("0x%08x", registradores[rs1] & 0xFFFFFFFFL); 
            			    String rs2Valor = String.format("0x%08x", registradores[rs2] & 0xFFFFFFFFL); 
            		
            			    logBuilder.append(String.format("%s:sltu   %s,%s,%s            %s=(%s<%s)=%d\n",
            			            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, resultado));
//            			    System.out.printf("%s:sltu   %s,%s,%s            %s=(%s<%s)=%d\n",
//            			            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, resultado);

	            		}
	            		else if(funct3 == 0b000 && funct7 == 0b0000000) {//ADD
         
		    			    int v1 = registradores[rs1];
		    			    int v2 = registradores[rs2];
	            			int resultado =  v1 + v2;

	            		    if (rd != 0) {
	            		        registradores[rd] = resultado;
	            		    }

	            		    String enderecoHex = String.format("0x%08x", PC + k);
	            		    String rdLabel = getRegistroLabel(rd);
	            		    String rs1Label = getRegistroLabel(rs1);
	            		    String rs2Label = getRegistroLabel(rs2);
	            		    String rs1Valor = String.format("0x%08x", v1);
	            		    String rs2Valor = String.format("0x%08x", v2);
	            		    String rdValor = String.format("0x%08x", resultado);
	            		            

	            		    logBuilder.append(String.format("%s:add    %s,%s,%s     %s=%s+%s=%s\n",
	            		            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor));
//	            		    System.out.printf("%s:add    %s,%s,%s     %s=%s+%s=%s\n",
//	            		            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor);

	            		}
	            		else if(funct3 == 0b000 && funct7 == 0b0100000) {//SUB
	            			 int resultado = registradores[rs1] - registradores[rs2];

	            			    if (rd != 0) {
	            			        registradores[rd] = resultado;
	            			    }

	            			    String enderecoHex = String.format("0x%08x", PC + k);
	            			    String rdLabel = getRegistroLabel(rd);
	            			    String rs1Label = getRegistroLabel(rs1);
	            			    String rs2Label = getRegistroLabel(rs2);
	            			    String rs1Valor = String.format("0x%08x", registradores[rs1]);
	            			    String rs2Valor = String.format("0x%08x", registradores[rs2]   & 0xFFFFFFFF);
	            			    String rdValor = (rd != 0)
	            			            ? String.format("0x%08x", registradores[rd])
	            			            : String.format("0x%08x", resultado);

	            			    logBuilder.append(String.format("%s:sub    %s,%s,%s     %s=%s-%s=%s\n",
	            			            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor));
//	            			    System.out.printf("%s:sub    %s,%s,%s     %s=%s-%s=%s\n",
//	            			            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor);
	            		}
	            		else if(funct3 == 0b000 && funct7 == 0b0000001) {//MUL
		            		    String rs1Valorw = String.format("0x%08x", registradores[rs1]);
		            		    String rs2Valorw = String.format("0x%08x", registradores[rs2]);
    							int resultado= registradores[rs1] * registradores[rs2];
    							
		        				if (rd != 0) {
		        					registradores[rd] = resultado;
		        				}
								String enderecoHex = String.format("0x%08x", (PC + k) & 0xFFFFFFFFL );
						        String rdLabel = getRegistroLabel(rd);
						        String rs1Label = getRegistroLabel(rs1);
						        String rs2Label = getRegistroLabel(rs2);
						        
						        String rdValor = String.format("0x%08x", registradores[rd]);
						        
						        logBuilder.append(String.format("%s:mul    %s,%s,%s            %s=%s*%s=%s\n", enderecoHex , rdLabel, rs1Label, rs2Label, rdLabel, rs1Valorw, rs2Valorw, rdValor));
						       //System.out.printf("%s:mul    %s,%s,%s            %s=%s*%s=%s\n", enderecoHex , rdLabel, rs1Label, rs2Label, rdLabel, rs1Valorw, rs2Valorw, rdValor);
	            		}
	            		else if(funct3 == 0b001 && funct7 == 0b0000001) { //MULH
	            			 	long multiplicando = (long) registradores[rs1];
	            			    long multiplicador = (long) registradores[rs2];
	            			    long produto = multiplicando * multiplicador;
	            			    int parteAlta = (int) (produto >> 32);

	            			    if (rd != 0) {
	            			        registradores[rd] = parteAlta;
	            			    }

	            			    String enderecoHex = String.format("0x%08x", PC + k);
	            			    String rdLabel = getRegistroLabel(rd);
	            			    String rs1Label = getRegistroLabel(rs1);
	            			    String rs2Label = getRegistroLabel(rs2);
	            			    String rs1Valor = String.format("0x%08x", registradores[rs1]);
	            			    String rs2Valor = String.format("0x%08x", registradores[rs2]);

	            			    String rdValor = (rd != 0)? String.format("0x%08x", registradores[rd]): String.format("0x%08x", parteAlta);

//	            			    System.out.printf("%s:mulh   %s,%s,%s     %s=%s*%s=%s\n",
//	            			            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor);
	            			    logBuilder.append(String.format("%s:mulh   %s,%s,%s            %s=%s*%s=%s\n",
	            			            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor));	            		
	            	    }
	            		else if (funct3 == 0b010 && funct7 == 0b0000001) { // MULHSU
	            		    long multiplicando = (long) registradores[rs1]; // signed
	            		    long multiplicador = Integer.toUnsignedLong(registradores[rs2]); // unsigned
	            		    long produto = multiplicando * multiplicador;
	            		    int parteAlta = (int) (produto >> 32); // Extrai os 32 bits mais altos

	            		    if (rd != 0) {
	            		        registradores[rd] = parteAlta;
	            		    }

	            		    // Formatação dos valores para exibição
	            		    String enderecoHex = String.format("0x%08x", PC + k);
	            		    String rdLabel = getRegistroLabel(rd);
	            		    String rs1Label = getRegistroLabel(rs1);
	            		    String rs2Label = getRegistroLabel(rs2);
	            		    String rs1Valor = String.format("0x%08x", registradores[rs1]);
	            		    String rs2Valor = String.format("0x%08x", registradores[rs2]);
	            		    String rdValor = (rd != 0) ? String.format("0x%08x", registradores[rd]) : String.format("0x%08x", parteAlta);

	            		    // Impressão e log da instrução executada
//	            		    System.out.printf("%s:mulhsu %s,%s,%s     %s=%s*%s=%s\n",
//	            		            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor);
	            		    logBuilder.append(String.format("%s:mulhsu %s,%s,%s     %s=%s*%s=%s\n",
	            		            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor));
	            		}
	            		else if (funct3 == 0b011 && funct7 == 0b0000001) { // MULHU (Unsigned * Unsigned)
	            		    long multiplicando = Integer.toUnsignedLong(registradores[rs1]); // rs1 como unsigned
	            		    long multiplicador = Integer.toUnsignedLong(registradores[rs2]); // rs2 como unsigned
	            		    long produto = multiplicando * multiplicador;
	            		    int parteAlta = (int) (produto >> 32); // Extrai os 32 bits mais altos

	            		    if (rd != 0) {
	            		        registradores[rd] = parteAlta;
	            		    }

	            		    // Formatação dos valores para exibição
	            		    String enderecoHex = String.format("0x%08x", PC + k);
	            		    String rdLabel = getRegistroLabel(rd);
	            		    String rs1Label = getRegistroLabel(rs1);
	            		    String rs2Label = getRegistroLabel(rs2);
	            		    String rs1Valor = String.format("0x%08x", registradores[rs1]);
	            		    String rs2Valor = String.format("0x%08x", registradores[rs2]);
	            		    String rdValor = (rd != 0) ? String.format("0x%08x", registradores[rd]) : String.format("0x%08x", parteAlta);

	            		    // Impressão e log da instrução executada
//	            		    System.out.printf("%s:mulhu  %s,%s,%s     %s=%s*%s=%s\n",
//	            		            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor);
	            		    logBuilder.append(String.format("%s:mulhu  %s,%s,%s     %s=%s*%s=%s\n",
	            		            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor));
	            		}
	            		else if (funct3 == 0b100 && funct7 == 0b0000001) { // DIV (Signed Division)
	            		    int dividend = registradores[rs1]; 
	            		    int divisor = registradores[rs2];  
	            		    int resultado;

	            		    if (divisor == 0) {
	            		        resultado = -1; // Em RISC-V, divisão por zero retorna 0xFFFFFFFF
	            		    } else if (dividend == Integer.MIN_VALUE && divisor == -1) {
	            		        resultado = Integer.MIN_VALUE; // Casos de overflow (-2147483648 / -1)
	            		    } else {
	            		        resultado = dividend / divisor;
	            		    }

	            		    if (rd != 0) {
	            		        registradores[rd] = resultado;
	            		    }

	            		    // Formatação para exibição
	            		    String enderecoHex = String.format("0x%08x", PC + k);
	            		    String rdLabel = getRegistroLabel(rd);
	            		    String rs1Label = getRegistroLabel(rs1);
	            		    String rs2Label = getRegistroLabel(rs2);
	            		    String rs1Valor = String.format("0x%08x", registradores[rs1]);
	            		    String rs2Valor = String.format("0x%08x", registradores[rs2]);
	            		    String rdValor = (rd != 0)
	            		            ? String.format("0x%08x", registradores[rd])
	            		            : String.format("0x%08x", resultado);

	            		    logBuilder.append(String.format("%s:div    %s,%s,%s          %s=%s/%s=%s\n",
	            		            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor));
//	            		    System.out.printf("%s:div    %s,%s,%s     %s=%s/%s=%s\n",
//	            		            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor);
	            		}

	            		else if(funct3 == 0b101 && funct7 == 0b0000001) { //DIVU
	            			 int dividend = registradores[rs1]; 
	            			    int divisor = registradores[rs2];  
	            			    int resultado;

	            			    if (divisor == 0) {
	            			        resultado = 0xFFFFFFFF;  
	            			    } else {
	            			        resultado = Integer.divideUnsigned(dividend, divisor);
	            			    }

	            			    if (rd != 0) {
	            			        registradores[rd] = resultado;
	            			    }

	            			    String enderecoHex = String.format("0x%08x", PC + k);
	            			    String rdLabel = getRegistroLabel(rd);
	            			    String rs1Label = getRegistroLabel(rs1);
	            			    String rs2Label = getRegistroLabel(rs2);
	            			    String rs1Valor = String.format("0x%08x", registradores[rs1]);
	            			    String rs2Valor = String.format("0x%08x", registradores[rs2]);
	            			    String rdValor = (rd != 0)
	            			            ? String.format("0x%08x", registradores[rd])
	            			            : String.format("0x%08x", resultado);

	            			    logBuilder.append(String.format("%s:divu   %s,%s,%s     %s=%s/%s=%s\n",
	            			            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor));
//	            			    System.out.printf("%s:divu   %s,%s,%s     %s=%s/%s=%s\n",
//	            			            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor);
	            		}
	            		else if(funct3 == 0b110 && funct7 == 0b0000001) {//REM
	            			  int dividend = registradores[rs1];
	            			    int divisor = registradores[rs2];
	            			    int resultado;

	            			    if (divisor == 0) {
	            			        resultado = dividend;  
	            			    } else {
	            			        resultado = dividend % divisor;
	            			    }

	            			    if (rd != 0) {
	            			        registradores[rd] = resultado;
	            			    }

	            			    String enderecoHex = String.format("0x%08x", PC + k);
	            			    String rdLabel = getRegistroLabel(rd);
	            			    String rs1Label = getRegistroLabel(rs1);
	            			    String rs2Label = getRegistroLabel(rs2);
	            			    String rs1Valor = String.format("0x%08x", registradores[rs1]);
	            			    String rs2Valor = String.format("0x%08x", registradores[rs2]);
	            			    String rdValor = (rd != 0)
	            			            ? String.format("0x%08x", registradores[rd])
	            			            : String.format("0x%08x", resultado);

	            			 // Corrigindo a formatação da saída para exibir corretamente rs2Valor
	            			    logBuilder.append(String.format("%s:rem    %s,%s,%s     %s=%s%%%s=%s\n",
	            			            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor));
//	            			    System.out.printf("%s:rem    %s,%s,%s     %s=%s%%%s=%s\n",
//	            			            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor);
	            			}
	            		else if(funct3 == 0b111 && funct7 == 0b0000001) {//REMU
	            			int dividend = registradores[rs1];
	            		    int divisor = registradores[rs2];
	            		    int resultado;

	            		    if (divisor == 0) {
	            		        resultado = dividend;
	            		    } else {
	            		        resultado = Integer.remainderUnsigned(dividend, divisor);
	            		    }

	            		    if (rd != 0) {
	            		        registradores[rd] = resultado;
	            		    }

	            		    String enderecoHex = String.format("0x%08x", PC + k);
	            		    String rdLabel = getRegistroLabel(rd);
	            		    String rs1Label = getRegistroLabel(rs1);
	            		    String rs2Label = getRegistroLabel(rs2);
	            		    String rs1Valor = String.format("0x%08x", registradores[rs1]);
	            		    String rs2Valor = String.format("0x%08x", registradores[rs2]);
	            		    String rdValor = (rd != 0)
	            		            ? String.format("0x%08x", registradores[rd])
	            		            : String.format("0x%08x", resultado);

	            		    logBuilder.append(String.format("%s:remu   %s,%s,%s            %s=%s%%%s=%s\n",
	            		            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor));
//	            		    System.out.printf("%s:remu   %s,%s,%s    %s=%s%%%s=%s\n",
//	            		            enderecoHex, rdLabel, rs1Label, rs2Label, rdLabel, rs1Valor, rs2Valor, rdValor);
	            		}
	            		else if(funct3 == 0b101 && funct7 == 0b0100000 ) {//SRA
	            			
	            				
	            		    	int deslocamento = registradores[rs2] & 0x1F;
	            			    String enderecoHex = String.format("0x%08x", PC + k);
	            			    
	            			    String rdLabel = getRegistroLabel(rd);
	            			    String rs1Label = getRegistroLabel(rs1);
	            			    String rs2Label = getRegistroLabel(rs2);
	            			    
	            			    String rs1Valor = String.format("0x%08x", registradores[rs1]);
	            			    String rs2Valor = String.format("0x%08x", registradores[rs2]);

	            			    int resultado = registradores[rs1] >> deslocamento;


	            			    if (rd != 0) {
	            			        registradores[rd] = resultado;
	            			    }

	            			    String rdValor = String.format("0x%08x", resultado);
	            			    String shiftAmountHex = String.format("0x%08x", deslocamento);
	            			    												    
	            			    String saida = String.format("%s:sra    %s,%s,%s            %s=%s>>>%d=%s\n",
	            	                    enderecoHex, rdLabel, rs1Label, rs2Label,
	            	                    rdLabel, rs1Valor, deslocamento, rdValor);
	            			    logBuilder.append(saida);
	            			    //System.out.printf(saida);
	            		}
	                    break;
            	case 0b0010011: //IMEDIATOS
            		pcUpdated = false; 
            	    int funct3I = (instruction >> 12) & 0b111;
            	    int rs1I = (instruction >> 15) & 0b11111;
            	    int rdI = (instruction >> 7) & 0b11111;     
            	    int immI = instruction >> 20; 
            	    int funct7I = (instruction >> 25) & 0x7F; 
            	    
            	    if ((immI & 0x800) != 0) {
        	    		immI |= 0xFFFFF000;
        	    	}
	            	    if (funct3I == 0b000) { // ADDI
	
	            	    	int regFonte = registradores[rs1I]; 
	            	    	if ((immI & 0x800) != 0) {
	            	    		immI |= 0xFFFFF000;
	            	    	}
	            	    	
	            	        int regDestino = regFonte + immI; 
	            	        if (rdI != 0) {
	            	        	
	            	        	registradores[rdI] = regDestino; 
	            	        }
	            	        
	            	        String enderecoHex = String.format("0x%08x", PC & 0xFFFFFFFFL);
	            	        String rdLabel = getRegistroLabel(rdI);       					
	            	        String rdValor = String.format("0x%08x", registradores[rdI]);   
	            	        String rs1Label = getRegistroLabel(rs1I);						
	            	        String rs1Valor = String.format("0x%08x", regFonte);  			  
	            	        String immFormatado = String.format("0x%03x", immI & 0Xfff);   
	            	        String immFormatado2 = String.format("0x%08x", immI);   
	            	        
//	            	        System.out.printf("%s:addi   %s,%s,%s         %s=%s+%s=%s\n", 
//	            	        		enderecoHex, rdLabel, rs1Label, immFormatado, rdLabel, rs1Valor, immFormatado2, rdValor);
	            	        
	            	        logBuilder.append(String.format("%s:addi   %s,%s,%s         %s=%s+%s=%s\n", 
           	                     enderecoHex, rdLabel, rs1Label, immFormatado, 
           	                     rdLabel, rs1Valor, immFormatado2, rdValor));
	            
	            	    }
	            	    else if (funct3I == 0b110) { // ORI         
	            	        int resultadoORI = registradores[rs1I] | immI;
	            	        if (rdI != 0) {  
	            	        	registradores[rdI] = resultadoORI;
	            	        }
	            	        
	            	        String enderecoHex = String.format("0x%08x", PC + k);
	            	        String rdLabel = getRegistroLabel(rdI);
	            	        String rs1Label = getRegistroLabel(rs1I);
	            	        String rs1Valor = String.format("0x%08x", registradores[rs1I]);
	            	        String rdValor = String.format("0x%08x", (rdI == 0) ? resultadoORI : registradores[rdI]);
	            	        String immFormat = String.format("0x%03x", immI  & 0xFFF);
	            	        
	            	        logBuilder.append(String.format("%s:ori   %s,%s,%s         %s=%s|0x%03x=%s\n", 
	            	        		enderecoHex, rdLabel, rs1Label, immFormat, rdLabel, rs1Valor, immI, rdValor));
//			            	        System.out.printf("%s:ori   %s,%s,%s         %s=%s|0x%03X=%s\n", 
//			            	        		enderecoHex, rdLabel, rs1Label, immFormat, rdLabel, rs1Valor, immI, rdValor);
	            	    }
            	    else if (funct3I == 0b111) { // ANDI 
            	
            	    	int immFormatado = immI & 0xFFF; // Mantém os 12 bits menos significativos
            	        if ((immI & 0x800) != 0) { // Extensão de sinal se o bit 11 estiver definido
            	            immFormatado |= 0xFFFFF000;
            	        }
            	        	int rs1ValorAntesO = registradores[rs1I];  

		    				int resultadoANDI = registradores[rs1I] & immI;
	            	        if (rdI != 0) {
	            	        	registradores[rdI] = resultadoANDI;
	            	        }
	            	        String enderecoHex = String.format("0x%08x", PC + k);
	            	        String rdLabel = getRegistroLabel(rdI);
	                        String rs1Label = getRegistroLabel(rs1I);
	                        String rs1Valor = String.format("0x%08x",rs1ValorAntesO);
	                        String rdValor = String.format("0x%08x", (rdI == 0) ? resultadoANDI : registradores[rdI]);
	            	        String immFormat = String.format("0x%03x", immI  & 0xFFF);

	                        
//	                        System.out.printf("%s:andi   %s,%s,%s         %s=%s&0x%08x=%s\n",
//	                        		enderecoHex, rdLabel, rs1Label, immFormat, rdLabel, rs1Valor, immFormatado, rdValor);
	                        logBuilder.append(String.format("%s:andi   %s,%s,%s         %s=%s&0x%08x=%s\n",
	                        		enderecoHex, rdLabel, rs1Label, immFormat, rdLabel, rs1Valor, immFormatado, rdValor));
            	    }
            	    else if(funct3I == 0b100) { //XORI
            	    	
            	    	int immFormatado = immI & 0xFFF; // Mantém os 12 bits menos significativos
            	        if ((immI & 0x800) != 0) { // Extensão de sinal se o bit 11 estiver definido
            	            immFormatado |= 0xFFFFF000;
            	        }
	            	    	int resultadoXORI = registradores[rs1I] ^ immI; 
	            	    	
	            	    	if (rdI != 0) {  
	            	    	    registradores[rdI] = resultadoXORI;
	            	    	}
    	    				String enderecoHex = String.format("0x%08x", PC + k);
    	    				String rdLabel = getRegistroLabel(rdI);
                            String rs1Label = getRegistroLabel(rs1I);
                            String rs1Valor = String.format("0x%08x", registradores[rs1I]);
                            String rdValor = String.format("0x%08x", (rdI == 0) ? resultadoXORI : registradores[rdI]);

//                            System.out.printf("%s:xori   %s,%s,0x%03x         %s=%s^0x%08x=%s\n", 
//                            		enderecoHex, rdLabel, rs1Label, (immFormatado & 0xFFF), rdLabel, rs1Valor, immFormatado, rdValor);
	                        logBuilder.append(String.format("%s:xori   %s,%s,0x%03x   %s=%s^0x%08x=%s\n", 
	                        		enderecoHex, rdLabel, rs1Label, (immFormatado & 0xFFF), rdLabel, rs1Valor, immFormatado, rdValor));

            	    }
            	    else if(funct3I == 0b001) { // SLLI
            	        int shamt = (instruction >> 20) & 0b11111; // Extrai o shift amount
            	        int resultado = registradores[rs1I] << shamt; // Aplica o shift lógico à esquerda

            	        if (rdI != 0) {
            	            registradores[rdI] = resultado; // Atualiza o registrador de destino, exceto se for x0
            	        }

            	        // Formatação das strings
            	        String enderecoHex = String.format("0x%08x", PC); 
            	        String rdLabel = getRegistroLabel(rdI);
            	        String rs1Label = getRegistroLabel(rs1I);
            	        String rs1Valor = String.format("0x%08x", registradores[rs1I]);
            	        String rdValor = String.format("0x%08x", resultado);
            	        String shamtDec = String.format("%d", shamt);

            	        // Imprime a instrução formatada
            	        if (resultado == 0) {
            	            logBuilder.append(String.format("%s:slli   %s,%s,%s            %s=%s<<%s=%s\n",
            	                    enderecoHex, rdLabel, rs1Label, shamtDec, rdLabel, rs1Valor, shamtDec, rdValor));
//            	            System.out.printf("%s:slli   %s,%s,%s            %s=%s<<%s=%s\n",
//            	                    enderecoHex, rdLabel, rs1Label, shamtDec, rdLabel, rs1Valor, shamtDec, rdValor);
            	        } else {
            	            logBuilder.append(String.format("%s:slli   %s,%s,%s        %s=%s<<%s=%s\n",
            	                    enderecoHex, rdLabel, rs1Label, shamtDec, rdLabel, rs1Valor, shamtDec, rdValor));
//            	            System.out.printf("%s:slli   %s,%s,%s        %s=%s<<%s=%s\n",
//            	                    enderecoHex, rdLabel, rs1Label, shamtDec, rdLabel, rs1Valor, shamtDec, rdValor);
            	        }
            	    }
            	    else if(funct3I == 0b101 ) {  // SRLI ou SRAI
    	    			 int shamt = (instruction >> 20) & 0b11111;
    	    			 int resultadoShift;
	            	     if (funct7I == 0b0000000) { //SRLI
	            	    	 String rs1Valorantes = String.format("0x%08x", registradores[rs1I]);
	            	    	 
	            	    	 resultadoShift = registradores[rs1I] >>> shamt;
	            	    	 
	            	    	 if (rdI != 0) {  
	            	             registradores[rdI] = resultadoShift;
	            	         }
			    				
			    				String enderecoHex = String.format("0x%08x", PC + k);
		                        String rdLabel = getRegistroLabel(rdI);
		                        String rs1Label = getRegistroLabel(rs1I);
		                        String rs1Valor = String.format("0x%08x", registradores[rs1I]);
		                        String rdValor = String.format("0x%08x", resultadoShift);
		                        
		                        logBuilder.append(String.format("%s:srli   %s,%s,%d   %s=%s>>%d=%s\n",
		                        							enderecoHex, rdLabel, rs1Label, shamt, rdLabel, rs1Valorantes, shamt, rdValor));
//		                        System.out.printf("%s:srli   %s,%s,%d   %s=%s>>%d=%s\n",
//		                        							enderecoHex, rdLabel, rs1Label, shamt, rdLabel, rs1Valorantes, shamt, rdValor);
	            	     }
            	        else if(funct7I == 0b0100000) {//SRAI
            	        	
	            	    	 String rs1Valorantes = String.format("0x%08x", registradores[rs1I]);

            	        	int valorReg1 = registradores[rs1I];
            	        	 resultadoShift =  valorReg1 >> shamt;
            	        	 
            	        	 if (rdI != 0) {  
            	                 registradores[rdI] = resultadoShift;
            	             }
            	            String enderecoHex = String.format("0x%08x", PC + k);
            	            String rdLabel = getRegistroLabel(rdI);
            	            String rs1Label = getRegistroLabel(rs1I);
            	            String rs1Valor = String.format("0x%08x", registradores[rs1I]);
            	            String rdValor = String.format("0x%08x",resultadoShift);
            	            
            	            logBuilder.append(String.format("%s:srai   %s,%s,%d   %s=%s>>>%d=%s\n", 
            	            				enderecoHex, rdLabel, rs1Label, shamt, rdLabel, rs1Valorantes, shamt, rdValor));
//            	            System.out.printf("%s:srai   %s,%s,%d   %s=%s>>%d=%s\n",
//            	            				enderecoHex, rdLabel, rs1Label, shamt, rdLabel, rs1Valorantes, shamt, rdValor);
            	        }
				    }
            	    else if(funct3I == 0b010) { //SLTI
            	    	
            	    	int resultado = (registradores[rs1I] < immI) ? 1 : 0;

            	        if (rdI != 0) {
            	            registradores[rdI] = resultado;
            	        }

            	        String enderecoHex = String.format("0x%08x", PC + k);
            	        String rdLabel = getRegistroLabel(rdI);
            	        String rs1Label = getRegistroLabel(rs1I);
            	        String rs1Valor = String.format("0x%08x", registradores[rs1I]);
            	        String rdValor = String.format("%d", resultado);
            	        String immHex = String.format("0x%03x", immI  & 0xFFF);
            	        String immHexOperando = String.format("0x%08x", immI);


            	        logBuilder.append(String.format("%s:slti   %s,%s,%s   %s=(%s<%s)=%s\n",
            	                			enderecoHex, rdLabel, rs1Label, immHex, rdLabel, rs1Valor, immHexOperando, rdValor));
//            	        System.out.printf("%s:slti   %s,%s,%s   %s=(%s<%s)=%s\n",
//            	                			enderecoHex, rdLabel, rs1Label, immHex, rdLabel, rs1Valor, immHexOperando, rdValor);
            	    }
            	    else if (funct3I == 0b011) { // SLTIU
            	        int immSignExtended = (immI << 20) >> 20; // Extensão de sinal para 32 bits
            	        long rs1ValueUnsigned = registradores[rs1I] & 0xFFFFFFFFL; // Trata rs1 como unsigned
            	        long immUnsignedLong = immSignExtended & 0xFFFFFFFFL; // Trata imm como unsigned

            	        int resultado = (rs1ValueUnsigned < immUnsignedLong) ? 1 : 0;
            	        
            	        if (rdI != 0) {
            	            registradores[rdI] = resultado; // Apenas atualiza se rd não for zero
            	        }

            	        // Formatação das strings
            	        String enderecoHex = String.format("0x%08x", PC);
            	        String rdLabel = getRegistroLabel(rdI);
            	        String rs1Label = getRegistroLabel(rs1I);
            	        String rs1Valor = String.format("0x%08x", registradores[rs1I]);
            	        String immHex = String.format("0x%03x", immSignExtended & 0xFFF	);
            	        String immHexOperando = String.format("0x%08x", immSignExtended );

            	        String rdValor = String.format("%d", resultado);

            	        // Imprime a instrução formatada
            	        logBuilder.append(String.format("%s:sltiu  %s,%s,%s        %s=(%s<%s)=%s\n", 
            	        				enderecoHex, rdLabel, rs1Label, immHex, rdLabel, rs1Valor, immHexOperando, rdValor));
//            	        
//            	        System.out.printf("%s:sltiu  %s,%s,%s        %s=(%s<%s)=%s\n", 
//            	        				enderecoHex, rdLabel, rs1Label, immHex, rdLabel, rs1Valor, immHexOperando, rdValor);
            	    }
            		break;
               case 0b1100011: //TIPO B
            	   
            	    pcUpdated = false;
            	    int funct3B = (instruction >> 12) & 0b111;  
            	    int rs1B = (instruction >> 15) & 0b11111;  
            	    int rs2B = (instruction >> 20) & 0b11111;  
            	    
            	    int immB = ((instruction >> 31) & 0x1 ) << 12  |  
            	            	((instruction >> 7) & 0x1) << 11  |  
            	            	((instruction >> 25) & 0x3F) << 5 | 
            	            	((instruction >> 8) & 0xf) << 1;
            	    
            	    int immOPerando = (instruction >> 8 ) & 0xF;
            	    
            	    if ((immB & 0x1000) != 0) {  
            	        immB |= 0xFFFFE000;  
            	    }
            	    
		    		if (funct3B == 0b000) { // BEQ
		    			
		    			long pcCondicao = 0x800004ac;
//		    			System.out.printf("ver valor 0x%08x:", PC );
//		    			System.out.println();
//		    			System.out.println("valor pc" + pcCondicao);
		    			long testePC = PC;
		    			if(PC == pcCondicao) {
		    				break;
		    			}
	    			  	long pcAntes = PC;  // Salva o valor do PC antes da execução da instrução
	    			    boolean condicao = (registradores[rs1B] == registradores[rs2B]); // Verifica se os registradores são iguais
	    			    
	    			    //System.out.printf("VALOR DO IMEDIATO --> 0x%08x (%d)\n", immB, immB);
	    			    if (condicao) {
	    			        PC = PC + immB; 
	    			    	pcUpdated = true;
	    			    } 
	    			    else {
	    			    	PC += 4;
	    			    	pcUpdated = true;
	    			    }

	    			    // Formatação dos valores para exibição
	    			    String enderecoHex = String.format("0x%08x", pcAntes);  // PC antes de ser atualizado
	    			    String rs1Label = getRegistroLabel(rs1B);
	    			    String rs2Label = getRegistroLabel(rs2B);
	    			    String rs1Valor = String.format("0x%08x", registradores[rs1B]);
	    			    String rs2Valor = String.format("0x%08x", registradores[rs2B]);
	    			    String immOPerandof = String.format("0x%03x", immOPerando & 0xFFF); // Imediato formatado corretamente
	    			    String pcDestino = String.format("0x%08x", PC);

	    			    // Impressão do resultado da instrução
//	    			    System.out.printf("%s:beq    %s,%s,%s         (%s==%s)=%d->pc=%s\n", 
//	    			            enderecoHex, rs1Label, rs2Label, immOPerandof, rs1Valor, rs2Valor, 
//	    			            condicao ? 1 : 0, pcDestino);

	    			    logBuilder.append(String.format("%s:beq    %s,%s,%s         (%s==%s)=%d->pc=%s\n", 
	    			            enderecoHex, rs1Label, rs2Label, immOPerandof, rs1Valor, rs2Valor, 
	    			            condicao ? 1 : 0, pcDestino));
	    			   
		    		}
				    else if(funct3B == 0b001) { //BNE	
				    	
				
				    	long pcAnteriorBne = PC;
            	    	int conteudoRegistrador1 =  registradores[rs1B];
            	    	int conteudoRegistrador2 =  registradores[rs2B];

            	        boolean condicao = (conteudoRegistrador1 != conteudoRegistrador2);
            	        
            	        if (condicao) {
            	        	PC = PC + immB; 
            	        	pcUpdated = true;
            	        	
            	        	 int imediatoBNE = ((instruction >> 4 ) & 0xFF) | 0xF00;
                 	        String enderecoHex = String.format("0x%08x", pcAnteriorBne);
                 	        String rs1Label = getRegistroLabel(rs1B);
                 	        String rs2Label = getRegistroLabel(rs2B);
                 	        String rs1Valor = String.format("0x%08x", conteudoRegistrador1);
                 	        String rs2Valor = String.format("0x%08x", conteudoRegistrador2);
                 	        String immFormatado = String.format("0x%03x", imediatoBNE  );
                 	        
//                 	        System.out.printf("IMEDIATO SEM FORMATACAO 0x%03x",  ((instruction >> 4 ) & 0xFF) | 0xF00 ) ;
//                 	        System.out.printf("IMEDIATO FORMATADO 0x%03x ", immB);
                 	        String pcDestino = String.format("0x%08x", PC);

                 	        // Impressão correta da instrução executada
//                 	        System.out.printf("%s:bne    %s,%s,%s       (%s!=%s)=%d->pc=%s\n", 
//                 	                enderecoHex, rs1Label, rs2Label, immFormatado, rs1Valor, rs2Valor, 
//                 	                condicao ? 1 : 0, pcDestino);

                 	        logBuilder.append(String.format("%s:bne    %s,%s,%s       (%s!=%s)=%d->pc=%s\n", 
                 	                enderecoHex, rs1Label, rs2Label, immFormatado, rs1Valor, rs2Valor, 
                 	                condicao ? 1 : 0, pcDestino));
            	        }
            	        else {
            	        	PC += 4;
	    			    	pcUpdated = true;
	    			    	
//	    			    	int imediatoBNE = ((instruction >> 4 ) & 0xFF) | 0xF00;
	            	        String enderecoHex = String.format("0x%08x", pcAnteriorBne);
	            	        String rs1Label = getRegistroLabel(rs1B);
	            	        String rs2Label = getRegistroLabel(rs2B);
	            	        String rs1Valor = String.format("0x%08x", conteudoRegistrador1);
	            	        String rs2Valor = String.format("0x%08x", conteudoRegistrador2);
	            	        String immFormatado = String.format("0x%03x", immOPerando  );
//	            	        
//	            	        System.out.printf("IMEDIATO SEM FORMATACAO 0x%03x",  ((instruction >> 4 ) & 0xFF) | 0xF00 ) ;
//	            	        System.out.printf("IMEDIATO FORMATADO 0x%03x ", immB);
	            	        String pcDestino = String.format("0x%08x", PC);

	            	        // Impressão correta da instrução executada
//	            	        System.out.printf("%s:bne    %s,%s,%s       (%s!=%s)=%d->pc=%s\n", 
//	            	                enderecoHex, rs1Label, rs2Label, immFormatado, rs1Valor, rs2Valor, 
//	            	                condicao ? 1 : 0, pcDestino);

	            	        logBuilder.append(String.format("%s:bne    %s,%s,%s       (%s!=%s)=%d->pc=%s\n", 
	            	                enderecoHex, rs1Label, rs2Label, immFormatado, rs1Valor, rs2Valor, 
	            	                condicao ? 1 : 0, pcDestino));
            	        }
            	        
            	        
	            	}
				    else if (funct3B == 0b100) { // BLT
				        int pcAnteriorB = (int) PC;
				        
				        if (registradores[rs1B] < registradores[rs2B]) {
				        	PC = PC + immB; 
				            pcUpdated = true;
				        } 
				        else {
				            PC += 4;
				            pcUpdated = true;
				        }

				        String enderecoHex = String.format("0x%08x", pcAnteriorB);   
				        String rs1Label = getRegistroLabel(rs1B);			   		 
				        String rs2Label = getRegistroLabel(rs2B);					// nome do reg 2
				        String rs1Valor = String.format("0x%08x", registradores[rs1B]); 	//valor reg 1 
				        String rs2Valor = String.format("0x%08x", registradores[rs2B]);		//valor reg 2 
				        int immExtendido = (immOPerando << 20) >> 20; // Extensão de sinal correta
				        String immFormatadoCorretamente = String.format("0x%03x", immExtendido & 0xFFF);

				        //String immFormatado = String.format("0x%03x", (immOPerando < 0 ? immOPerando + 0x1000 : immOPerando) & 0xFFF);
				        logBuilder.append(String.format("%s:blt    %s,%s,%s         (%s<%s)=%d->pc=0x%08x\n", 
	                			enderecoHex, rs1Label, rs2Label, immFormatadoCorretamente, 
	                			rs1Valor, rs2Valor, (registradores[rs1B] < registradores[rs2B] ? 1 : 0), PC));
//				        System.out.printf("%s:blt    %s,%s,%s         (%s<%s)=%d->pc=0x%08x\n", 
//				                			enderecoHex, rs1Label, rs2Label, immformatadoCorretamente, 
//				                			rs1Valor, rs2Valor, (registradores[rs1B] < registradores[rs2B] ? 1 : 0), PC);
				     
				    }
				    else if (funct3B == 0b101) { // 
				    	
				        long pcAntes = PC;
				        boolean condicao = (registradores[rs1B] >= registradores[rs2B]);

				        if (condicao) {
				        	PC = PC + immB; 
				            pcUpdated = true;
				        } else {
				            PC += 4;
				            pcUpdated = true;
				        }

				        // Formatação dos valores para exibição
				        String enderecoHex = String.format("0x%08x", pcAntes);
				        String rs1Label = getRegistroLabel(rs1B);
				        String rs2Label = getRegistroLabel(rs2B);
				        String rs1Valor = String.format("0x%08x", registradores[rs1B]);
				        String rs2Valor = String.format("0x%08x", registradores[rs2B]);
				        String immFormatado = String.format("0x%03x", immOPerando & 0xFFF);
				        String pcDestino = String.format("0x%08x", PC);

				        // Impressão do resultado da instrução
//				        System.out.printf("%s:bge    %s,%s,%s        (%s>=%s)=%d->pc=%s\n", 
//				                enderecoHex, rs1Label, rs2Label, immFormatado, rs1Valor, rs2Valor, 
//				                condicao ? 1 : 0, pcDestino);

				        logBuilder.append(String.format("%s:bge    %s,%s,%s        (%s>=%s)=%d->pc=%s\n", 
				                enderecoHex, rs1Label, rs2Label, immFormatado, rs1Valor, rs2Valor, 
				                condicao ? 1 : 0, pcDestino));
				 
				    }
				    else if (funct3B == 0b110) { // BLTU - Branch if Less Than Unsigned
				        long pcAntes = PC;
				        boolean condicao = Integer.compareUnsigned(registradores[rs1B], registradores[rs2B]) < 0;

				        if (condicao) {
				            PC = PC + immB;
				        } else {
				            PC += 4;
				        }
				        pcUpdated = true;

				        // Impressão do resultado da instrução BLTU
				        String enderecoHex = String.format("0x%08x", pcAntes);
				        String rs1Label = getRegistroLabel(rs1B);
				        String rs2Label = getRegistroLabel(rs2B);
				        String rs1Valor = String.format("0x%08x", registradores[rs1B]);
				        String rs2Valor = String.format("0x%08x", registradores[rs2B]);
				        String immFormatado = String.format("0x%03x", immOPerando & 0xFFF);
				        String pcDestino = String.format("0x%08x", PC);

//				        System.out.printf("%s:bltu   %s,%s,%s       (%s<%s)=%d->pc=%s\n", 
//				                enderecoHex, rs1Label, rs2Label, immFormatado, rs1Valor, rs2Valor, 
//				                condicao ? 1 : 0, pcDestino);

				        logBuilder.append(String.format("%s:bltu   %s,%s,%s       (%s<%s)=%d->pc=%s\n", 
				                enderecoHex, rs1Label, rs2Label, immFormatado, rs1Valor, rs2Valor, 
				                condicao ? 1 : 0, pcDestino));
				    }
				    else if (funct3B == 0b111) { // BGEU - Branch if Greater or Equal Unsigned
				    	
				        long pcAntes = PC;
				        boolean condicao = Integer.compareUnsigned(registradores[rs1B], registradores[rs2B]) >= 0;

				        if (condicao) {
				            PC = PC + immB;
				            pcUpdated = true;
				        } else {
				            PC += 4;
				            pcUpdated = true;
				        }
				       

				        // Impressão do resultado da instrução BGEU
				        String enderecoHex = String.format("0x%08x", pcAntes);
				        String rs1Label = getRegistroLabel(rs1B);
				        String rs2Label = getRegistroLabel(rs2B);
				        String rs1Valor = String.format("0x%08x", registradores[rs1B]);
				        String rs2Valor = String.format("0x%08x", registradores[rs2B]);
				        String immFormatado = String.format("0x%03x", immOPerando & 0xFFF);
				        String pcDestino = String.format("0x%08x", PC);

//				        System.out.printf("%s:bgeu   %s,%s,%s       (%s>=%s)=%d->pc=%s\n", 
//				                enderecoHex, rs1Label, rs2Label, immFormatado, rs1Valor, rs2Valor, 
//				                condicao ? 1 : 0, pcDestino);

				        logBuilder.append(String.format("%s:bgeu   %s,%s,%s       (%s>=%s)=%d->pc=%s\n", 
				                enderecoHex, rs1Label, rs2Label, immFormatado, rs1Valor, rs2Valor, 
				                condicao ? 1 : 0, pcDestino));
				    }
				    break;
               case 0b0110111:  // LUI
            	   pcUpdated = false;
            	    int rdL = (instruction >> 7) & 0b11111;  // Registrador de destino (bits 11-7)
            	    int immL = instruction & 0xFFFFF000;    
            	    int immLAntesDeslocamento = (immL >>> 12) & 0xFFFFF;  // Apenas os bits superiores

            	    if (rdL != 0) {  
            	        registradores[rdL] = immL;
            	        
            	    }
            	    String enderecoHex = String.format("0x%08x", PC);  
            	    String nomeRdLui = getRegistroLabel(rdL);
            	    
            	    logBuilder.append(String.format("%s:lui    %s,0x%05x          %s=0x%08x%n", 
            	        enderecoHex, nomeRdLui, immLAntesDeslocamento, nomeRdLui, immL));
//            	    System.out.printf("%s:lui    %s,0x%05x          %s=0x%08x%n", 
//            	        enderecoHex, nomeRdLui, immLAntesDeslocamento, nomeRdLui, immL);
            	    
            	    break;
            	case 0b0010111: // AUIPC
            		pcUpdated = false;
            	    int rdAuipc = (instruction >> 7) & 0x1F;
            	    int immAuipc = (instruction & 0xFFFFF000) >> 12;
            	    int immAuipcAntesDeslocamento = immAuipc;
            	    //System.out.printf("immAuipc antes do deslocamento: 0x%08x%n", immAuipc);

            	    if ((instruction & 0x80000000) != 0) { 
            	        immAuipc |= 0xFFF00000; 
            	    }
            	    immAuipc = immAuipc << 12;
            	   
            	    int enderecoFinal =(int) PC + immAuipc;
            	    registradores[rdAuipc] =  enderecoFinal;

            	    String nomeRdAuipc = getRegistroLabel(rdAuipc);
            	    logBuilder.append(String.format("0x%08x:auipc  %s,0x%05x          %s=0x%08x+0x%08x=0x%08x%n", PC, nomeRdAuipc, immAuipcAntesDeslocamento, nomeRdAuipc, PC, immAuipc, enderecoFinal));
            	    //System.out.printf("0x%08x:auipc  %s,0x%05x          %s=0x%08x+0x%08x=0x%08x%n", PC, nomeRdAuipc, immAuipcAntesDeslocamento, nomeRdAuipc, PC, immAuipc, enderecoFinal);
            	    break;
  
            	case 0b0000011: 
            		pcUpdated = false;
            		int rdL1 = (instruction >> 7) & 0b11111;   
            		int funct3L1 = (instruction >> 12) & 0b111;   
            		int rs1L1 = (instruction >> 15) & 0b11111;
            		int immL1 = (instruction >> 20) ; 
  
//            		 if ((immL1 & 0x800) != 0) { 
//            		        immL1 &=  0xFFF;
//            		    }
            		
            		 
            	    if (funct3L1 == 0b010) { // LW - Load Word
            	        int enderecoMemoriaL = (registradores[rs1L1] + immL1) ;
            	        int indiceMemoria = menosOffset(enderecoMemoriaL) ;
            	        int valorLidoL = 0;
            	        boolean enderecoValido = true;
            	        
            	        if (indiceMemoria < 0 || indiceMemoria + 3 >= memoria.length) {
            	            enderecoValido = false;
            	        } 
            	        else {
            	            valorLidoL = readMemory32(indiceMemoria);
            	            
    	            		PC += 4;
    	            		pcUpdated = true;
            	        }
            	        
            	        int formatSaidaImmL1 = immL1 & 0xFFF ;
            	             
            	        if (enderecoValido) {
            	            if (rdL1 != 0) {  
            	            	registradores[rdL1] = valorLidoL;
            	            }
            	            long endPC = (PC - 4) & 0xFFFFFFFFL;
            	            String nomeRdL1 = getRegistroLabel(rdL1);
            	            String nomeRs1L1 = getRegistroLabel(rs1L1);
            	            
            	            
            	            logBuilder.append(String.format("0x%08x:lw     %s,0x%03x(%s)        %s=mem[0x%08x]=0x%08x\n", 
            	            		endPC, nomeRdL1, formatSaidaImmL1 , nomeRs1L1, nomeRdL1 , enderecoMemoriaL, (int) valorLidoL));
            	            
//            	            System.out.printf("0x%08x:lw     %s,0x%03x(%s)        %s=mem[0x%08x]=0x%08x\n", 
//            	            		endPC, nomeRdL1, formatSaidaImmL1  , nomeRs1L1, nomeRdL1, enderecoMemoriaL, (int) valorLidoL);
            	        }
            	        else {
            	        	long endPC = PC & 0xFFFFFFFFL;
            	        	System.out.println();
            	            System.out.println("nao é valido");
            	            String nomeRdL1 = getRegistroLabel(rdL1);
            	            String nomeRs1L1 = getRegistroLabel(rs1L1);
            	            logBuilder.append(String.format("0x%08x:lw     %s,0x%03x(%s)  %s=mem[0x%08x]=0x%08x", 
            	            		endPC, nomeRdL1, formatSaidaImmL1, nomeRs1L1, nomeRdL1, enderecoMemoriaL, registradores[rdL1]));
            	            /*System.out.printf("0x%08x:lw     %s,0x%03x(%s)  %s=mem[0x%08x]=0x%08x", 
            	            		endPC, nomeRdL1, formatSaidaImmL1, nomeRs1L1, nomeRdL1, enderecoMemoriaL, registradores[rdL1]);*/
            	        }
            	    }
            	    	else if (funct3L1 == 0b000) { // LB - Load Byte
            	    	
            	        int enderecoMemoriaLBSaida = registradores[rs1L1] + immL1 ; 
            	        int enderecoMemoriaLB = menosOffset(enderecoMemoriaLBSaida);
            	        
            	        int valorLidoLB = 0;
            	        boolean enderecoValidoLB = enderecoMemoriaLB >= 0 && enderecoMemoriaLB < memoria.length;
            	        
            	        if (enderecoValidoLB) {
            	        	valorLidoLB = readMemory8(enderecoMemoriaLB );
            	        	
        	        	if ((valorLidoLB & 0x80) != 0) {
        	        		 valorLidoLB |= 0xFFFFFF00;
        	        	
        	        	}
        	            valorLidoLB |= 0xFFFFFF00;


            	        } if (rdL1 != 0 && enderecoValidoLB) {
            	            registradores[rdL1] = valorLidoLB;
            	        }

          	            long endPCLB = PC;
            	        String nomeRdL1 = getRegistroLabel(rdL1);
            	        String nomeRs1L1 = getRegistroLabel(rs1L1);
            	        String enderecoMemoriaHex = String.format("0x%08x", enderecoMemoriaLBSaida);
            	        String rdValor = String.format("0x%08x", valorLidoLB);
/*
            	        System.out.printf("%s:lb     %s,0x%03x(%s)        %s=mem[%s]=%s\n",
            	                String.format("0x%08x", endPCLB), nomeRdL1, immL1, nomeRs1L1, nomeRdL1, enderecoMemoriaHex, rdValor);*/

            	        logBuilder.append(String.format("%s:lb     %s,0x%03x(%s)        %s=mem[%s]=%s\n",
            	                String.format("0x%08x", endPCLB), nomeRdL1, immL1, nomeRs1L1, nomeRdL1, enderecoMemoriaHex, rdValor));
            	    
            	    }
            	    else if (funct3L1 == 0b100) { // LBU - Load Byte Unsigned
            	        // Calcula o endereço de memória
            	    	
            	    	int enderecoMemoriaSaida = registradores[rs1L1] + immL1;
            	        int enderecoMemoriaLBU = menosOffset( registradores[rs1L1] + immL1);
            	        final boolean enderecoValidoLBU = enderecoMemoriaLBU >= 0 && enderecoMemoriaLBU < memoria.length;

            	        int valorLidoLBU;
            	        if (enderecoValidoLBU) {
            	            valorLidoLBU = readMemory8(enderecoMemoriaLBU) & 0x000000FF; // Converte para inteiro sem sinal
            	        } else {
            	            valorLidoLBU = 0xDEADBEEF; 
            	        }
            	        if (rdL1 != 0) {
            	            registradores[rdL1] = valorLidoLBU;
            	        }
            	        
            	       

            	        // Obtém nomes dos registradores
            	        long endPCLBU = PC & 0xFFFFFFFFL;
            	        String nomeRdL1 = getRegistroLabel(rdL1);
            	        String nomeRs1L1 = getRegistroLabel(rs1L1);

            	        // Exibição da instrução e do valor lido
            	        if (enderecoValidoLBU) {
            	        	 logBuilder.append(String.format("0x%08x:lbu    %s,0x%03x(%s)        %s=mem[0x%08x]=0x%08x\n",
                 	                endPCLBU, nomeRdL1, immL1, nomeRs1L1, nomeRdL1, enderecoMemoriaSaida, valorLidoLBU));
            	            //System.out.printf("0x%08x:lbu    %s,0x%03x(%s)        %s=mem[0x%08x]=0x%08x\n",endPCLBU, nomeRdL1, immL1, nomeRs1L1, nomeRdL1, enderecoMemoriaSaida, valorLidoLBU);
            	        } 
            	    }
            	    else if (funct3L1 == 0b001) { // LH - Load Halfword (com extensão de sinal)
            	        int enderecoMemoriaL = (registradores[rs1L1] + immL1) & 0xFFFFFFFF;
            	        int indiceMemoria = enderecoMemoriaL - 0x80000000;
            	        boolean enderecoValido = (indiceMemoria >= 0 && indiceMemoria + 1 < memoria.length); // Apenas 2 bytes

            	        if (enderecoValido) {
            	            int halfword =  readMemory16(indiceMemoria); // Leitura com extensão de sinal
            	            int valorLidoL = halfword; // Conversão automática para 32 bits com sinal
            	            PC += 4;
            	            pcUpdated = true;
            	            
            	            if ((valorLidoL & 0x80) != 0) {
            	            	valorLidoL |= 0xFFFF0000;
           	              	}

            	            if (rdL1 != 0) {  
            	                registradores[rdL1] = valorLidoL;
            	            }
            	            long endPC = (PC - 4) & 0xFFFFFFFFL;
            	            String nomeRdL1 = getRegistroLabel(rdL1);
            	            String nomeRs1L1 = getRegistroLabel(rs1L1);

            	            logBuilder.append(String.format("0x%08x:lh     %s,0x%03x(%s)        %s=mem[0x%08x]=0x%08x\n", 
            	                endPC, nomeRdL1, immL1, nomeRs1L1, nomeRdL1, enderecoMemoriaL, valorLidoL));
//
//            	            System.out.printf("0x%08x:lh     %s,0x%03x(%s)        %s=mem[0x%08x]=0x%08x\n", 
//            	                endPC, nomeRdL1, immL1, nomeRs1L1, nomeRdL1, enderecoMemoriaL, valorLidoL);
            	        } else {
            	            long endPC = PC & 0xFFFFFFFFL;
            	            //System.out.println("Endereço inválido para LH.");
            	        }
            	    }

            	    else if (funct3L1 == 0b101) { // LHU - Load Halfword Unsigned (sem extensão de sinal)
            	        int enderecoMemoriaL = (registradores[rs1L1] + immL1) & 0xFFFFFFFF;
            	        int indiceMemoria = enderecoMemoriaL - 0x80000000;
            	        boolean enderecoValido = (indiceMemoria >= 0 && indiceMemoria + 1 < memoria.length); // Apenas 2 bytes

            	        if (enderecoValido) {
            	            int halfword = readMemory16(indiceMemoria); // Leitura sem extensão de sinal
            	            int valorLidoL = halfword & 0xFFFF; // Mantém apenas os 16 bits sem sinal
            	            PC += 4;
            	            pcUpdated = true;

            	            if (rdL1 != 0) {  
            	                registradores[rdL1] = valorLidoL;
            	            }
            	            long endPC = (PC - 4) & 0xFFFFFFFFL;
            	            String nomeRdL1 = getRegistroLabel(rdL1);
            	            String nomeRs1L1 = getRegistroLabel(rs1L1);

            	            logBuilder.append(String.format("0x%08x:lhu    %s,0x%03x(%s)        %s=mem[0x%08x]=0x%08x\n", 
            	                endPC, nomeRdL1, immL1, nomeRs1L1, nomeRdL1, enderecoMemoriaL, valorLidoL));

//            	            System.out.printf("0x%08x:lhu    %s,0x%03x(%s)        %s=mem[0x%08x]=0x%08x\n", 
//            	                endPC, nomeRdL1, immL1, nomeRs1L1, nomeRdL1, enderecoMemoriaL, valorLidoL);
            	        } else {
            	            long endPC = PC & 0xFFFFFFFFL;
            	            //System.out.println("Endereço inválido para LHU.");
            	        }
            	    }
            	break;
            	case 0b0100011: //SW
            		pcUpdated = false;
            		 
            		 int imm_11_5 = (instruction >> 25) & 0b1111111; 
                     int rs2SW = (instruction >> 20) & 0b11111; 
                     int funct3_s = (instruction >> 12) & 0b111;
                     int rs1SW = (instruction >> 15) & 0b11111;       
                     int imm_4_0 = (instruction >> 7) & 0b11111;   
                     int offsetS = (imm_11_5 << 5) | imm_4_0; 

                     if ((offsetS & (1 << 11)) != 0) { 
                    	 offsetS |= 0xFFFFF000; // Extensão de sinal para 32 bits
                     }
                     
                     if(funct3_s == 0b010) { //SW
	                     int enderecoMemoria = registradores[rs1SW] + (offsetS); 
	                     int valorSW = (rs2SW == 0) ? 0 : registradores[rs2SW];   
	                     
	                     int indiceMemoria =  menosOffset(enderecoMemoria);
	                     
	                     if (indiceMemoria < 0 || indiceMemoria +3 >= memoria.length) {
	                         
	                     } 
	                     else {
	                         writeMemory(indiceMemoria, valorSW);
	                         int valorLidoE = 0;
	                         for (int i = 0; i < 4; i++) {
	                             valorLidoE |= (memoria[indiceMemoria + i] & 0xFF) << (i * 8);
	                         }
	                     }
	                     
//	                     System.out.printf("0x%08x:sw     %s,0x%03x(%s)      mem[0x%08x]=0x%08x\n",
//	                    	        PC, getRegistroLabel(rs2SW),formatarSaida12bits(offsetS) , getRegistroLabel(rs1SW), enderecoMemoria, readMemory(indiceMemoria));
	                     
	                     logBuilder.append(String.format("0x%08x:sw     %s,0x%03x(%s)      mem[0x%08x]=0x%08x\n",
	                 	        PC, getRegistroLabel(rs2SW), formatarSaida12bits(offsetS), getRegistroLabel(rs1SW), enderecoMemoria, valorSW));

                     }
                     else if(funct3_s == 001) { //SH
                    	 int enderecoMemoria = registradores[rs1SW] + (offsetS); 
	                     int valorSW = (rs2SW == 0) ? 0 : registradores[rs2SW];   
	                     
	                     int indiceMemoria =  menosOffset(enderecoMemoria);
	                     if (indiceMemoria < 0 || indiceMemoria +3 >= memoria.length) {
	                         
	                     } 
	                     else {
	                    	 writeMemory16(indiceMemoria,valorSW);
	                    	 int valorLidoE = 0;
	                         for (int i = 0; i < 2; i++) {
	                             valorLidoE |= (memoria[indiceMemoria + i] & 0xFF) << (i * 8);
	                         }
	                     }
	                      
//	                     System.out.printf(
//	                             "0x%08x:sh     %s,0x%03x(%s)      mem[0x%08x]=0x%04x\n",
//	                             PC, getRegistroLabel(rs2SW), formatarSaida12bits(offsetS), getRegistroLabel(rs1SW), enderecoMemoria,readMemory16(indiceMemoria));
	                         
	                         logBuilder.append(String.format(
	                             "0x%08x:sh     %s,0x%03x(%s)      mem[0x%08x]=0x%04x\n",
	                             PC, getRegistroLabel(rs2SW), formatarSaida12bits(offsetS), getRegistroLabel(rs1SW), enderecoMemoria,readMemory16(indiceMemoria)));
                     }
                     else if(funct3_s == 0b000) { //sb
                    	 int enderecoMemoria = registradores[rs1SW] + (offsetS); 
	                     int valorSW = (rs2SW == 0) ? 0 : registradores[rs2SW];   
	                     
	                     int indiceMemoria =  menosOffset(enderecoMemoria);
	                     if (indiceMemoria < 0 || indiceMemoria +3 >= memoria.length) {
	                         
	                     } 
	                     else {
	                    	 writeMemory8(indiceMemoria,valorSW);
	                    	 int valorLidoE = memoria[indiceMemoria] & 0xFF; 
	                     }
	                     
	                     int formatSaidaOffset = formatarSaida12bits(offsetS);
	                     
//	                     System.out.printf(
//	                             "0x%08x:bb     %s,0x%03x(%s)      mem[0x%08x]=0x%02x\n",
//	                             PC, getRegistroLabel(rs2SW), formatSaidaOffset, getRegistroLabel(rs1SW), enderecoMemoria,readMemory8(indiceMemoria));
	                         
	                         logBuilder.append(String.format(
	                             "0x%08x:sb     %s,0x%03x(%s)      mem[0x%08x]=0x%02x\n",
	                             PC, getRegistroLabel(rs2SW), formatSaidaOffset, getRegistroLabel(rs1SW), enderecoMemoria,readMemory8(indiceMemoria)));
                     }
                     break;
            	case 0b1110011:
                     int funct3EB = (instruction >> 12) & 0b111;
                     int immIEB = instruction >> 20;
                     String enderecoHexEB = String.format("0x%08X", PC + k);

            		if (funct3EB == 0b000) {
            			 if (funct3EB == 0b000 && immIEB == 1) { // EBREAK
            				 logBuilder.append(String.format("%s:ebreak%n", enderecoHexEB));
                             //System.out.printf("%s:ebreak%n", enderecoHexEB);
                             run = false; // Para a execução
                         }
                    }
                    break;
               default:
            	   run = false;
   			}
            if (!pcUpdated) {
                PC += 4;  
            }
            //run++;
            loop++;
        } 
    	 //System.out.println(logBuilder);
    	 Leitura.appendToFile(caminhoArquivoSaida, logBuilder.toString());
    }
    
    private String getRegistroLabel(int reg) {
        String[] labels = { "zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6" };
        return labels[reg];
    }
    
    private int readMemory (int indexMem) { 
    	int valorLido2 = 0;
        for (int i = 0; i < 4; i++) {
        	valorLido2 |= (memoria[indexMem + i ] & 0xFF) << (i * 8);
        }
        return valorLido2;
    }
    
    private int readMemory32(int indexMem) { 
        if (indexMem < 0 || indexMem >= memoria.length - 3) {
            throw new IndexOutOfBoundsException("Índice fora dos limites da memória");
        }

        // Lê quatro bytes em little-endian
        int byte0 = memoria[indexMem] & 0xFF;       // Byte menos significativo (LSB)
        int byte1 = memoria[indexMem + 1] & 0xFF;
        int byte2 = memoria[indexMem + 2] & 0xFF;
        int byte3 = memoria[indexMem + 3] & 0xFF;  // Byte mais significativo (MSB)

        // Monta o valor 32-bit corretamente em little-endian
        int result = (byte3 << 24) | (byte2 << 16) | (byte1 << 8) | byte0;

        //System.out.printf("Leitura de memória no índice %d: 0x%08X\n", indexMem, result);
        return result;
    }
    

    private int readMemory16(int indexMem) { 
        if (indexMem < 0 || indexMem >= memoria.length - 1) {
            throw new IndexOutOfBoundsException("Índice fora dos limites da memória");
        }
        int lowByte = memoria[indexMem] & 0xFF;       
        int highByte = memoria[indexMem + 1] & 0xFF;  
        
        int result = (highByte << 8) | lowByte; 
        
       // System.out.printf("Leitura de memória no índice %d: 0x%04X\n", indexMem, result);
        return result;
    }
        
    
    
    private int readMemory8(int indexMem) { 
        //System.out.printf("teste t0: 0x%08x \n",indexMem);
        return (memoria[  indexMem ] & 0xFF);
    }
    
    
    private int readMemoryTeste(int indexMem) { 
        int valorLido2 = 0;
        for (int i = 0; i < 4; i++) {
            valorLido2 |= (memoria[indexMem + i] & 0xFF) << (8 * (3 - i));
        }
        return valorLido2;
    }

    private void writeMemory(int indexMem, int valorMem) { 
    	for (int i = 0; i < 4; i++) {
            memoria[indexMem + i] = (byte) ((valorMem >> (i * 8)) & 0xFF);
        }
    }
    
    private void writeMemory16(int indexMem, int valorMem) {
        if (indexMem < 0 || indexMem >= memoria.length - 1) {
            throw new IndexOutOfBoundsException("Índice fora dos limites da memória");
        }

        int lowByte = valorMem & 0xFF;          // - sig
        int highByte = (valorMem >> 8) & 0xFF;   //mais sig
        
        memoria[indexMem] = (byte) lowByte;
        memoria[indexMem + 1] = (byte) highByte;
    }

    private void writeMemory8(int indexMem, int valorMem) {
        if (indexMem < 0 || indexMem >= memoria.length) {
            throw new IndexOutOfBoundsException("Índice fora dos limites da memória");
        }
        memoria[indexMem] = (byte) (valorMem & 0xFF);

        //System.out.printf("Escrita na memória no índice %d: 0x%02X\n", indexMem, valorMem & 0xFF);
    }

    private int menosOffset(int valor)  {
        return (int) (valor - (offset & 0xFFFFFFFFL));
    }
    
    private int maisOffset(int valor) {
        return (int) (valor + (offset) & 0xFFFFFFFFL);

    }
    private int formatarSaida12bits(int valor) {
    	return valor & 0xFFF ;
    }
}






