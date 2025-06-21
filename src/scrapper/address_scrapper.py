from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
import time
import re
import csv
from bs4 import BeautifulSoup

# # Abrir navegador
driver = webdriver.Chrome()
# Lê as URLs de url_coletadas.txt
with open("urls_coletadas.txt", "r", encoding="utf-8") as f:
    lista_urls = [linha.strip() for linha in f.readlines() if linha.strip()]

info_enderecos = []
for url in lista_urls:
    print(f"🔗 Acessando: {url}")
    driver.get(url) # Acessar página
    time.sleep(2)
    try:
        
        # Acessa iframe
        iframe = driver.find_element(By.NAME, "tabIframe3")  # ou use um seletor mais específico
        driver.switch_to.frame(iframe)

        # Pegar Codigo, Nome da instituição e Sigla
        instituicao_info = driver.find_element(By.XPATH, '//*[@id="listar_ies_endereco"]/table/tbody/tr[2]/td/table/tbody/tr/td[2]')
        # Faz o parser
        # Captura o HTML bruto da <td>
        html = instituicao_info.get_attribute("innerHTML")
        soup = BeautifulSoup(html, "html.parser")

        # Remove todas as divs (e tudo dentro delas)
        for div in soup.find_all("div"):
            div.decompose()

        # Extrai o texto limpo restante
        texto = soup.get_text().replace("\xa0", " ").strip()
        print(texto)
        codigo_instituicao = texto.split(")")[0].replace("(", "")
        nome = texto.split(")")[1].split("-")[0]
        sigla = texto.split("-")[-1].strip().split(" ")[0].split("\n")[0]
        # Seleciona todos os tbodys da tabela
        tbodys = driver.find_elements(By.CSS_SELECTOR, '#listar-ies-cadastro tbody')

        for i, tbody in enumerate(tbodys):
            linhas = tbody.find_elements(By.TAG_NAME, "tr")
            
            for j, linha in enumerate(linhas):
                colunas = linha.find_elements(By.TAG_NAME, "td")
                dados = [td.get_attribute("textContent").strip() for td in colunas]
                # Pega as colunas desejadas
                codigo_endereco = dados[0]
                denominicao = dados[1]
                endereco = dados[2]
                polo = dados[3]
                municipio = dados[4]
                uf = dados[5]
               
                row = [codigo_instituicao, nome, sigla, codigo_endereco, denominicao, endereco, polo, municipio, uf]
                print(f"Linha Extraída:\n{row}")
                info_enderecos.append(row)

    except Exception as e:
        print(f"⚠️ Erro ao processar {url}: {e}")

    finally:
        driver.switch_to.default_content()



# Salva tudo num CSV
with open("enderecos_extraidos.csv", "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow([
        "Código IES", 
        "Nome", 
        "Sigla",
        "Código Endereço", 
        "Denominação",
        "Endereço",
        "Polo", 
        "Município", 
        "UF"
    ])
    writer.writerows(info_enderecos)

print("Dados salvos em 'enderecos_extraidos.csv'")
driver.quit()
