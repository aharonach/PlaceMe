import React from 'react';
import {useParams, useNavigate, Outlet} from "react-router-dom";
import Loading from "../Loading";
import {Alert, Button, ButtonGroup} from "react-bootstrap";
import {LinkContainer} from 'react-router-bootstrap';
import useFetchRecord from "../../hooks/useFetchRecord";

export default function GroupPage() {
    let { groupId } = useParams();
    const [group, error, loading, axiosFetch] = useFetchRecord({
        fetchUrl: `/groups/${groupId}`,
        displayFields: ['name']
    });

    const navigate = useNavigate();

    const handleDelete = () => {
        axiosFetch({
            method: 'delete',
            url: `/groups/${groupId}`,
        }).then(() => navigate('/groups', {replace: true}));
    }

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && group &&
                <article className="group">
                    <h1>{group.name} (ID: {group.id})</h1>
                    <ButtonGroup>
                        <LinkContainer to={`/groups/${group.id}/edit`}><Button>Edit Group</Button></LinkContainer>
                        <LinkContainer to={`/groups/${group.id}/preferences`}><Button>Preferences</Button></LinkContainer>
                        <Button as="a" variant="danger" onClick={handleDelete}>Delete Group</Button>
                    </ButtonGroup>
                    <Outlet context={{group, error, loading, axiosFetch}} />
                </article>
            }
        </>
    )
}
