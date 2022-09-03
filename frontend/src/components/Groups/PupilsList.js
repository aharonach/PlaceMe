import React, {useState} from 'react';
import {useOutletContext} from "react-router-dom";
import DeletePupilFromGroup from "./DeletePupilFromGroup";
import columns from "../Pupils/columns";
import RecordList from "../RecordList";

export default function PupilsList() {
    const { group } = useOutletContext();
    const [updated, setUpdated] = useState(false);

    const pupilColumns = {
        ...columns,
        'actions': {
            label: "",
            callbacks: [
                (pupil) => <DeletePupilFromGroup key={`delete-${pupil.id}`} pupilId={pupil.id} groupId={group.id} updated={updated} setUpdated={setUpdated}>Unlink</DeletePupilFromGroup>
            ]
        }
    }

    return <RecordList
            fetchUrl={`/groups/${group.id}/pupils`}
            propertyName="pupilList"
            title={<h3>Pupils in the group</h3>}
            columns={pupilColumns}
            basePath="/pupils/"
            linkField="givenId"
            updated={updated}
            sorting={['firstName', 'lastName', 'givenId', 'createdTime', 'gender', 'birthDate']}
        />;
}