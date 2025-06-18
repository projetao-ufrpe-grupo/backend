from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
from bs4 import BeautifulSoup
import requests

# # Abrir navegador
driver = webdriver.Chrome()


# # Acessar página
driver.get("https://emec.mec.gov.br/emec/consulta-cadastro/detalhamento/d96957f455f6405d14c6542552b0f6eb/Mzg4MQ==")
time.sleep(4)

# botao_endereco = driver.find_element(By.ID, "tab_estatica_endereco")
# botao_endereco.click()

iframe = driver.find_element(By.NAME, "tabIframe3")  # ou use um seletor mais específico
driver.switch_to.frame(iframe)

# Seleciona todos os tbodys da tabela
tbodys = driver.find_elements(By.CSS_SELECTOR, '#listar-ies-cadastro tbody')

for i, tbody in enumerate(tbodys):
    linhas = tbody.find_elements(By.TAG_NAME, "tr")
    
    for j, linha in enumerate(linhas):
        colunas = linha.find_elements(By.TAG_NAME, "td")
        dados = [td.get_attribute("textContent").strip() for td in colunas]
        print(f"Tbody {i+1}, Linha {j+1}: {dados}")

driver.switch_to.default_content()
time.sleep(1000)


