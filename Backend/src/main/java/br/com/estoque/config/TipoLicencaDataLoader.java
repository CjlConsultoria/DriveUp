package br.com.estoque.config;

import br.com.estoque.model.TipoLicenca;
import br.com.estoque.model.enums.CodigoTipoLicenca;
import br.com.estoque.repository.TipoLicencaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TipoLicencaDataLoader implements CommandLineRunner {

    private final TipoLicencaRepository tipoLicencaRepository;

    public TipoLicencaDataLoader(TipoLicencaRepository tipoLicencaRepository) {
        this.tipoLicencaRepository = tipoLicencaRepository;
    }

    @Override
    public void run(String... args) {
        if (tipoLicencaRepository.count() == 0) {
            List<TipoLicenca> licencas = Arrays.asList(
                    new TipoLicenca("Licença Free", CodigoTipoLicenca.FREE, 1, "Plano gratuito com funcionalidades básicas."),
                    new TipoLicenca("Licença Basic", CodigoTipoLicenca.BASIC, 5, "Plano básico com recursos essenciais."),
                    new TipoLicenca("Licença Standard", CodigoTipoLicenca.STANDARD, 10, "Plano padrão para pequenas empresas."),
                    new TipoLicenca("Licença Premium", CodigoTipoLicenca.PREMIUM, 50, "Plano avançado com recursos completos."),
                    new TipoLicenca("Licença Enterprise", CodigoTipoLicenca.ENTERPRISE, 200, "Plano corporativo personalizado.")
            );

            tipoLicencaRepository.saveAll(licencas);
            System.out.println("Licenças padrão criadas com sucesso.");
        }
    }
}
