/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sessionbeans;

import entities.File;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author pingeso
 */
@Local
public interface FileFacadeLocal {

    void create(File file);

    void edit(File file);

    void remove(File file);

    File find(Object id);

    List<File> findAll();

    List<File> findRange(int[] range);

    int count();
    
}
