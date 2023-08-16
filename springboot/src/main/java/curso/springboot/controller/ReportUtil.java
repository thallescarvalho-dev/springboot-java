package curso.springboot.controller;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import curso.springboot.model.Pessoa;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class ReportUtil implements Serializable{

	/*Retorna nosso PDF em Byte para download no navegador*/
	public byte[] gerarRelatorio (List listDados, 
			String relatorio, ServletContext servletContex) 
			throws Exception{

		/*Cria a lista de dados para o relatorio com nossa lista de objetos para imprimir*/
		JRBeanCollectionDataSource jrbcds = new JRBeanCollectionDataSource(listDados);

		/*Carrega o caminho do arquivo jasper compilado*/
		String caminhoJasper = servletContex.getRealPath("relatorios") 
				+ File.separator + relatorio + ".jasper";

		/*Carrefa o arquivo jasper passando os dados*/
		JasperPrint impressoraJasper = JasperFillManager.fillReport(caminhoJasper, new HashMap(), jrbcds);

		/*Exporta para byte[] para fazer download do pdf*/
		return JasperExportManager.exportReportToPdf(impressoraJasper);
	}
}