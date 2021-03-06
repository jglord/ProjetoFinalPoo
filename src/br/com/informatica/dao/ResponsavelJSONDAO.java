package br.com.informatica.dao;

import br.com.informatica.exception.DAOException;
import br.com.informatica.model.Cliente;
import br.com.informatica.model.Responsavel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.StringProperty;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponsavelJSONDAO implements ResponsavelDAO {

    private Gson gson = new GsonBuilder().registerTypeAdapter(StringProperty.class, new StringPropertyAdapter()).create();

    private static final Path STORAGE_FILE = Paths.get("out//production//ProjetoFinalPoo//br//com//informatica//data//responsaveis.json");

    @Override
    public List<Responsavel> load() {
        List<Responsavel> responsaveis = new ArrayList<Responsavel>();
        try {
            Type listResponsavelType = new TypeToken<List<Responsavel>>(){}.getType();
            responsaveis = gson.fromJson( Files.newBufferedReader(STORAGE_FILE),listResponsavelType );
        }catch (Exception e){
            throw new DAOException(e);
        }
        return responsaveis;
    }

    @Override
    public void store(List<Responsavel> list) {
        Type listResponsavelType = new TypeToken<List<Responsavel>>() {
        }.getType();
        String json = gson.toJson(list, listResponsavelType);
        try {
            Files.write(STORAGE_FILE, json.getBytes());

        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List filter(String filtro) {
        String text = filtro.toUpperCase();
        return load().stream().filter(
                responsavel -> responsavel.getNome().toUpperCase().contains(text) ||
                        responsavel.getTelefone().toUpperCase().contains(text)
        ).collect(Collectors.toList());
    }

    @Override
    public void delete(int id) {
        List<Responsavel> responsaveis = this.load();

        Map<String, Responsavel> map = new HashMap<String, Responsavel>();

        responsaveis.forEach(c -> {
            map.put(String.valueOf(c.getId()), c);
        });
        responsaveis.remove(map.get(String.valueOf(id)));
        store(responsaveis);
    }

    @Override
    public int generateId() {
        return load().stream().mapToInt(c -> c.getId() + 1).max().orElse(1);
    }

}
