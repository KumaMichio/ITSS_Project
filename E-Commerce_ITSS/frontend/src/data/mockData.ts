import type { Product } from '../types';

export const mockProducts: Product[] = [
    {
        id: 1,
        title: "Effective Java - Third Edition",
        price: 450000,
        category: "Books",
        imageURL: "f1.png",
        quantity: 25,
        entryDate: "2024-01-15",
        dimension: 2.3,
        weight: 0.6
    },
    {
        id: 2,
        title: "Clean Code: A Handbook of Agile Software Craftsmanship",
        price: 380000,
        category: "Books",
        imageURL: "bôk.jpg",
        quantity: 30,
        entryDate: "2024-01-20",
        dimension: 2.1,
        weight: 0.5
    },
    {
        id: 3,
        title: "The Beatles - Abbey Road",
        price: 250000,
        category: "Music",
        imageURL: "cd2.png",
        quantity: 15,
        entryDate: "2024-02-01",
        dimension: 1.2,
        weight: 0.1
    },
    {
        id: 4,
        title: "Pink Floyd - The Dark Side of the Moon",
        price: 280000,
        category: "Music",
        imageURL: "cd3.png",
        quantity: 12,
        entryDate: "2024-02-05",
        dimension: 1.2,
        weight: 0.1
    },
    {
        id: 5,
        title: "The Matrix Trilogy",
        price: 350000,
        category: "Movies",
        imageURL: "dvd1.png",
        quantity: 20,
        entryDate: "2024-02-10",
        dimension: 1.5,
        weight: 0.15
    },
    {
        id: 6,
        title: "Inception",
        price: 180000,
        category: "Movies",
        imageURL: "dvd2.jpg",
        quantity: 18,
        entryDate: "2024-02-12",
        dimension: 1.4,
        weight: 0.12
    },
    {
        id: 7,
        title: "The Lord of the Rings: The Fellowship of the Ring",
        price: 420000,
        category: "Books",
        imageURL: "bok2.jpg",
        quantity: 8,
        entryDate: "2024-02-15",
        dimension: 2.8,
        weight: 0.9
    },
    {
        id: 8,
        title: "Queen - Bohemian Rhapsody",
        price: 320000,
        category: "Music",
        imageURL: "cd4.png",
        quantity: 14,
        entryDate: "2024-02-18",
        dimension: 1.2,
        weight: 0.1
    }
];

export default mockProducts;
