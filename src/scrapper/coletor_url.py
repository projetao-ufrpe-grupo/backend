from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time

# Abrir navegador
driver = webdriver.Chrome()
URLS = []

# Acessar página
driver.get("https://emec.mec.gov.br/emec/nova")

# Esperar um pouco para a página carregar
time.sleep(8) # Aperte F5 para recarregar a página logo que iniciar 

# Setar configurações de pesquisa

## Selecionar UF Pernambuco
seletor_UF = driver.find_element(By.ID, "sel_sg_uf")

select = Select(seletor_UF)
select.select_by_value("PE")
time.sleep(2)


## Clicar em pesquisar
botao_pesquisar = driver.find_element(By.ID, "btnPesqAvancada")
botao_pesquisar.click()

# Esperar até 10 segundos o elemento com ID "tbDataGridNova" aparecer
tabela = WebDriverWait(driver, 120).until(
    EC.presence_of_element_located((By.ID, "tbDataGridNova"))
)
time.sleep(5)
# Definir tabela para tamanho máximo (300)
seletor_tamanho = driver.find_element(By.ID, "paginationItemCountItemdiv_listar_consulta_avancada")
select = Select(seletor_tamanho)
select.select_by_value("/emec/nova-index/listar-consulta-avancada/list/300")

# Dispara o evento 'change' manualmente
driver.execute_script("arguments[0].dispatchEvent(new Event('change'))", seletor_tamanho)
time.sleep(30)

linhas = driver.find_elements(By.CSS_SELECTOR, "#tbyDados tr")
for i, linha in enumerate(linhas):
    try:
        # Localiza o último td e dentro dele o <img>
        img = linha.find_element(By.CSS_SELECTOR, "td:last-child img")

        # Scroll até o elemento (opcional, mas ajuda no clique)
        driver.execute_script("arguments[0].scrollIntoView(true);", img)
        
        # Salva o identificador da aba atual
        aba_original = driver.current_window_handle
        # Clica na imagem
        img.click()
        # 3. Espera até que uma nova aba seja aberta
        WebDriverWait(driver, 10).until(lambda d: len(d.window_handles) > 1)

        # Encontra o identificador da nova aba e troca para ela
        abas = driver.window_handles
        nova_aba = [aba for aba in abas if aba != aba_original][0]
        driver.switch_to.window(nova_aba)

        # 5. Espera a URL carregar e imprime
        WebDriverWait(driver, 10).until(lambda d: d.current_url != "about:blank")
        print("🔗 URL da nova aba:", driver.current_url)
        URLS.append(driver.current_url)
        # (Opcional) Fecha a nova aba e volta para a original
        driver.close()
        # Espera o pop-up carregar (opcional: pode trocar por espera inteligente)
    

        # Fecha o pop-up (se for uma nova aba ou janela, lidamos depois)
        # Aqui você pode extrair info, lidar com a janela, etc.
        driver.switch_to.window(aba_original)  # volta pro contexto principal, se for iframe
        print(f"[{i+1}] Instituição OK")

    except Exception as e:
        print(f"[{i+1}] Erro: {e}")


# Salva em um arquivo texto, um por linha
with open("urls_coletadas.txt", "w", encoding="utf-8") as f:
    for url in URLS:
        f.write(url + "\n")

print("URLs salvas em 'urls_coletadas.txt'")