import React, {useState} from 'react';
import {useOutletContext} from "react-router-dom";
import {PupilsList} from "../Pupils";
import DeletePupilFromGroup from "./DeletePupilFromGroup";

export default function PupilList() {
    const { group } = useOutletContext();
    const [updated, setUpdated] = useState(false);

    const pupilColumns = {
        'actions': {
            label: "",
            callbacks: [
                (pupil) => <DeletePupilFromGroup key={`delete-${pupil.id}`} pupilId={pupil.id} groupId={group.id} updated={updated} setUpdated={setUpdated}>Unlink</DeletePupilFromGroup>
            ]
        }
    }

    return (
        <>
            <h3>Pupils in the group</h3>
            <PupilsList
                fetchUrl={`/groups/${group.id}/pupils`}
                addButton={false}
                additionalColumns={pupilColumns}
                updated={updated}
            />
        </>
    )
}