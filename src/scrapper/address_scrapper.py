from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
import re
info_enderecos = []

# # Abrir navegador
driver = webdriver.Chrome()


# # Acessar página
driver.get("https://emec.mec.gov.br/emec/consulta-cadastro/detalhamento/d96957f455f6405d14c6542552b0f6eb/Mzg4MQ==")
time.sleep(6)

# Acessa iframe
iframe = driver.find_element(By.NAME, "tabIframe3")  # ou use um seletor mais específico
driver.switch_to.frame(iframe)

# Pegar Codigo, Nome da instituição e Sigla
instituicao_info = driver.find_element(By.XPATH, '//*[@id="listar_ies_endereco"]/table/tbody/tr[2]/td/table/tbody/tr/td[2]')
texto = instituicao_info.get_attribute("innerText").replace("\xa0", " ").strip()
print(texto)
padrao = r"\((\d+)\)\s*(.*?)\s*-\s*(.+)"
match = re.match(padrao, texto)

if match:                        # Exemplo de formato
    codigo = match.group(1)      # "3881"
    nome = match.group(2).strip()  # "Centro Universitário FIS"
    sigla = match.group(3).strip() # "Unifis"
else:
    codigo = nome = sigla = ""

# Seleciona todos os tbodys da tabela
tbodys = driver.find_elements(By.CSS_SELECTOR, '#listar-ies-cadastro tbody')

for i, tbody in enumerate(tbodys):
    linhas = tbody.find_elements(By.TAG_NAME, "tr")
    
    for j, linha in enumerate(linhas):
        colunas = linha.find_elements(By.TAG_NAME, "td")
        dados = [td.get_attribute("textContent").strip() for td in colunas]
        dados.extend([codigo, nome, sigla])
        print(f"{dados}")

driver.switch_to.default_content()
time.sleep(1000)


