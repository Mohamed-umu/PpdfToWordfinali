package com.example.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Service
public class SystemConverter {
	
		
	SystemConverter() throws IOException{
		
			
	}
	
	
	public void ConvertirToWord(File pdf) throws IOException {

	    String apiKey = "FWC00TXgFbZR3t9u4YWguiXJWcdPtOIX";
	    if (apiKey == null) {
	        throw new RuntimeException("API KEY no definida");
	    }

	    String url = "https://v2.convertapi.com/convert/pdf/to/docx?Secret=" + apiKey;

	    // Spring HTTP cliente
	    org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

	    // Cabeceras
	    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
	    headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

	    // Cuerpo
	    org.springframework.util.MultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
	    body.add("File", new org.springframework.core.io.FileSystemResource(pdf));

	    // Petición
	    org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, Object>> request =
	            new org.springframework.http.HttpEntity<>(body, headers);

	    // Enviar
	    String response = restTemplate.postForObject(url, request, String.class);

	    // EXTRAER BASE64
	    String clave = "\"FileData\":\"";
	    int inicio = response.indexOf(clave);

	    if (inicio == -1) {
	        throw new RuntimeException("No se encontró FileData");
	    }

	    inicio += clave.length();
	    int fin = response.indexOf("\"", inicio);

	    String base64 = response.substring(inicio, fin);

	    byte[] archivo = java.util.Base64.getDecoder().decode(base64);

	    FileOutputStream fos = new FileOutputStream("sal.docx");
	    fos.write(archivo);
	    fos.close();

	    System.out.println("Word creado correctamente");
	}
	
	
	public ResponseEntity<Resource> enviar(MultipartFile fil, SystemConverter file) throws IOException {
		 // 1️⃣ PDF temporal
	    File tempPdf = File.createTempFile("upload-", ".pdf");
	    fil.transferTo(tempPdf);

	    // 2️⃣ convertir (genera sal.docx)
	    file.ConvertirToWord(tempPdf);

	    File word = new File("sal.docx");
	    if (!word.exists() || word.length() == 0) {
	        throw new IOException("El Word generado está vacío");
	    }

	    // 3️⃣ leer en memoria
	    byte[] bytes = java.nio.file.Files.readAllBytes(word.toPath());
	    Resource resource = new org.springframework.core.io.ByteArrayResource(bytes);

	    // 4️⃣ nombre FINAL = nombre del PDF
	    String nombreFinal = fil.getOriginalFilename();
	    if (nombreFinal == null) {
	        nombreFinal = "archivo.docx";
	    } else {
	        nombreFinal = nombreFinal
	                .replaceAll("(?i)\\.pdf$", "") // quita .pdf / .PDF
	                + ".docx";
	    }

	    // 5️⃣ respuesta
	    ResponseEntity<Resource> response = ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION,
	                    "attachment; filename=\"" + nombreFinal + "\"")
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .contentLength(bytes.length)
	            .body(resource);

	    // 6️⃣ limpieza
	    tempPdf.delete();
	    word.delete();
	    
	    return response;
	}


}
