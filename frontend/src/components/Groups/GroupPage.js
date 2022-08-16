import React, {useEffect, useState} from 'react';
import {useParams, useNavigate} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import Loading from "../Loading";
import {Alert, Button} from "react-bootstrap";
import EditGroup from "./EditGroup";
import {PupilsList} from "../Pupils";
import DeletePupilFromGroup from "./DeletePupilFromGroup";

export default function GroupPage() {
    let { groupId } = useParams();
    const [group, error, loading, axiosFetch] = useAxios();
    let navigate = useNavigate();
    const [updated, setUpdated] = useState(false);

    const pupilColumns = {
        'actions': {
            label: "",
            callbacks: [
                (pupil) => <DeletePupilFromGroup key={`delete-${pupil.id}`} pupilId={pupil.id} groupId={groupId} updated={updated} setUpdated={setUpdated}>Unlink</DeletePupilFromGroup>
            ]
        }
    }

    const getGroup = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/groups/${groupId}`,
        });
    }

    const handleDelete = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'delete',
            url: `/groups/${groupId}`,
        }).then(() => navigate('/groups', {replace: true}));
    }

    useEffect(() => {
        getGroup();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && group &&
                <article className="group">
                    <h2>{group.name} (ID: {group.id})</h2>
                    <Button variant="danger" onClick={handleDelete}>Delete Group</Button>
                    <EditGroup group={group} />
                    <h3>Pupils in the group</h3>
                    <PupilsList fetchUrl={`/groups/${group.id}/pupils`} addButton={false} additionalColumns={pupilColumns} updated={updated} />
                </article>
            }
        </>
    )
}
