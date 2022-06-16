package fr.dila.reponses.core.service.vocabulary;

import fr.dila.reponses.api.service.vocabulary.GroupePolitiqueService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class GroupePolitiqueComponent
    extends ServiceEncapsulateComponent<GroupePolitiqueService, GroupePolitiqueServiceImpl> {

    public GroupePolitiqueComponent() {
        super(GroupePolitiqueService.class, new GroupePolitiqueServiceImpl());
    }
}
