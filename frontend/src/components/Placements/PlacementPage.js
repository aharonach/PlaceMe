import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import Loading from "../Loading";
import {Alert, Button} from "react-bootstrap";
import EditPlacement from './EditPlacement';
import PlacementData from "./PlacementData";
import {LinkContainer} from "react-router-bootstrap";

export default function PlacementPage({edit=false}){
    let { placementId } = useParams();
    const [placement, error, loading, axiosFetch] = useAxios();
    const [count, setCount] = useState(1);

    let navigate = useNavigate();

    const getPlacement = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/placements/${placementId}`,
        });
    }

    const handleDelete = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'delete',
            url: `/placements/${placement.id}`,
        }).then(() => navigate('/placements', {replace: true}));
    }

    useEffect(() => {
        getPlacement();
    }, [count]);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && placement &&
                <article>
                    <h2>{placement.name}</h2>

                    { !edit &&
                        <div>
                            <p>
                                <LinkContainer to="edit"><Button>Edit Placement</Button></LinkContainer>
                                <LinkContainer to="results"><Button>Show All Optional Results</Button></LinkContainer>
                                <Button variant="danger" onClick={handleDelete}>Delete Placement</Button>
                            </p>
                            <PlacementData placement={placement} />
                        </div>
                    }

                    { edit &&
                        <EditPlacement placement={placement} count={count} setCount={setCount} />
                    }
                </article>
            }
        </>
    );
}