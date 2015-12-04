package com.gamfig.carga;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Carga {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			

			File fXmlFile = new File("data/AnoAtual11.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("DESPESAS");
			Node despesasRoot = nList.item(0);

			NodeList despesas = despesasRoot.getChildNodes();

			System.out.println("----------------------------");

			PrintWriter writer = criaArquivo();
			PrintWriter writer2 = criaArquivo2();
			
			int total = 0;
			int controle = 0;
			boolean escreve ;
			String virgula=",";
			HashMap<String, Politico> politicos = new HashMap<String, Politico>();
			ArrayList<String> ids = new ArrayList<String>();
			@SuppressWarnings("unused")
			String idCadastro = null,dataEmisao = null,numAno,numMes,valorLiquido,numSubcota,txtDescricao,beneficiario,cnpj = null,sql = null,numero,nomeDeputado=null;
			for (int temp = 0; temp < despesas.getLength(); temp++) {
				escreve = true;
				
				Node nNode = despesas.item(temp);

				//System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					total++;
					controle++;
					Element eElement = (Element) nNode;
					//eElement.getChildNodes()
					// System.out.println("txNomeParlamentar : " + eElement.getElementsByTagName("txNomeParlamentar").item(0).getTextContent());
					//tem cota por liderança. Tem que tratar dps. Se nao tiver id cadastro é pq é gasto de liderança
					if (eElement.getElementsByTagName("ideCadastro").item(0) != null) {
						idCadastro = eElement.getElementsByTagName("ideCadastro").item(0).getTextContent();
						nomeDeputado = eElement.getElementsByTagName("txNomeParlamentar").item(0).getTextContent(); 
						Politico p = new Politico(nomeDeputado, idCadastro);
						if(!politicos.containsKey(idCadastro)){
							politicos.put(idCadastro, p);
							ids.add(idCadastro);
						}
					}else{
						idCadastro="0";
						//escreve = false;
					}
					System.out.print(String.valueOf(idCadastro));

					
					
					if (eElement.getElementsByTagName("datEmissao").item(0) != null) {
						dataEmisao = eElement.getElementsByTagName("datEmissao").item(0).getTextContent();
					}else{
						dataEmisao = "1111-11-11";
					}
					numero = eElement.getElementsByTagName("txtNumero").item(0).getTextContent();
					numSubcota = eElement.getElementsByTagName("numSubCota").item(0).getTextContent();
					txtDescricao = eElement.getElementsByTagName("txtDescricao").item(0).getTextContent();
					if (eElement.getElementsByTagName("txtBeneficiario").item(0) != null) {
						beneficiario = eElement.getElementsByTagName("txtBeneficiario").item(0).getTextContent();
					}else{
						beneficiario = "";
						System.out.println("sem benef");
					}
					if (eElement.getElementsByTagName("txtCNPJCPF").item(0) != null) {
						cnpj = eElement.getElementsByTagName("txtCNPJCPF").item(0).getTextContent();
						if(cnpj.length()==0)
							cnpj = "0";
					}else{
						cnpj = "0";
					}
					numMes = eElement.getElementsByTagName("numMes").item(0).getTextContent();
					numAno = eElement.getElementsByTagName("numAno").item(0).getTextContent();
					valorLiquido = eElement.getElementsByTagName("vlrLiquido").item(0).getTextContent();
					if(controle == 1000){
						virgula="; ";
						
						
					}else{
						if(temp == (despesas.getLength()-1))
							virgula="";
						else
							virgula = ",";
					}
					
					if(escreve){
						String output =  "("+idCadastro+",'"+cnpj+"','"+beneficiario.replace("'", "")+"','"+dataEmisao.substring(0, 10)+"',"+numAno+","+valorLiquido+","+numSubcota+",'"+numero.replace("'", "")+"',"+numMes+")"+virgula;
						writer.println(output);
					}
					if(controle == 1000){
						//writer = criaArquivo();
						controle=0;
						writer.println("insert into tb_cota (id_cadastro_cota,cnpj,nm_beneficiario,dt_emissao,nu_ano,vl_liquido,id_subcota,txtNumero,nu_mes)  values ");
						
					}
					
				}
			}
			
//			for (String id : ids) {				
//			
//				writer2.println("update tb_politico set id_cadastro_cota = "+id+" where nm_politico = \""+politicos.get(id).getNome()+"\";");
//			}
			System.out.println("total : " + total);
			//System.out.println(sql);
			writer.close();
			writer2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	

	}
	
	public static PrintWriter criaArquivo() throws FileNotFoundException, UnsupportedEncodingException{
		String date = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
		PrintWriter writer = new PrintWriter("output/"+date+".sql", "UTF-8");
		writer.println("insert into tb_cota (id_politico,cnpj,nm_beneficiario,dt_emissao,nu_ano,vl_liquido,id_subcota,txtNumero,nu_mes)  values ");
		
		return writer;
	}
	
	public static PrintWriter criaArquivo2() throws FileNotFoundException, UnsupportedEncodingException{
		String date = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
		PrintWriter writer = new PrintWriter("output/dep_"+date+".sql", "UTF-8");
		//writer.println("update tb_politico set id_cadastro_cota =  (id_politico,cnpj,nm_beneficiario,dt_emissao,nu_ano,vl_liquido,id_subcota,txtNumero,nu_mes)  values ");
		
		return writer;
	}

}
