import React, {useEffect} from 'react';
import {useParams, useNavigate, Outlet} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import Loading from "../Loading";
import {Alert, Button} from "react-bootstrap";
import {LinkContainer} from 'react-router-bootstrap';

export default function GroupPage() {
    let { groupId } = useParams();
    const [group, error, loading, axiosFetch] = useAxios();
    let navigate = useNavigate();

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
                    <LinkContainer to="preferences"><Button>Preferences</Button></LinkContainer>
                    <Outlet context={{group}} />
                </article>
            }
        </>
    )
}
