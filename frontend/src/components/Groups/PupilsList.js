import React, {useState} from 'react';
import {useOutletContext} from "react-router-dom";
import DeletePupilFromGroup from "./DeletePupilFromGroup";
import columns from "../Pupils/columns";
import RecordList from "../RecordList";
import {mapPupils} from "../Pupils/PupilsList";
import {Alert, Button, Modal} from "react-bootstrap";
import Attributes from '../Pupils/Attributes';

export default function PupilsList() {
    const { group } = useOutletContext();
    const [updated, setUpdated] = useState(false);
    const [pupilId, setPupilId] = useState();

    const pupilColumns = {
        ...columns,
        actions: {
            label: "",
            callbacks: [
                (pupil) => <Button
                    key={`edit-${pupil.id}`}
                    size="sm"
                    onClick={() => setPupilId(pupil.id)}
                >Edit Attributes</Button>,
                (pupil) => <DeletePupilFromGroup
                    key={`delete-${pupil.id}`}
                    pupilId={pupil.id}
                    groupId={group.id}
                    updated={updated}
                    setUpdated={setUpdated}
                >Unlink</DeletePupilFromGroup>
            ]
        }
    }

    return (
        <>
            <RecordList
                fetchUrl={`/groups/${group.id}/pupils`}
                propertyName="pupilList"
                title={<h3>Pupils in the group</h3>}
                columns={pupilColumns}
                basePath="/pupils/"
                linkField="givenId"
                updated={updated}
                mapCallback={mapPupils}
                hero={<Alert variant="info">No pupils in group.</Alert>}
                sorting={['firstName', 'lastName', 'givenId', 'createdTime', 'gender', 'birthDate']}
            />
            <Modal show={pupilId}>
                <Modal.Header closeButton onHide={() => setPupilId(null)}><Modal.Title>Edit Pupil Attributes</Modal.Title></Modal.Header>
                <Modal.Body>{pupilId && <Attributes pupilId={pupilId} group={group} />}</Modal.Body>
            </Modal>
        </>
    );
}